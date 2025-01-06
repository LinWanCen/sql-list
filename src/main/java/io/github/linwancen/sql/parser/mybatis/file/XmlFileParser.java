package io.github.linwancen.sql.parser.mybatis.file;

import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.log.SqlInfoLog;
import io.github.linwancen.sql.parser.mybatis.file.sql.SqlParamBuilder;
import io.github.linwancen.sql.parser.mybatis.file.sql.XmlChange;
import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.git.GitUtils;
import io.github.linwancen.util.xml.JdomUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLIncludeTransformer;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.contrib.input.LineNumberElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class XmlFileParser {
    private static final Logger LOG = LoggerFactory.getLogger(XmlFileParser.class);
    private static final XMLMapperEntityResolver MAPPER_ENTITY_RESOLVER = new XMLMapperEntityResolver();

    public final Configuration configuration;
    public final File file;
    public final GitRootInfo gitRootInfo;
    public final XPathParser xPathParser;
    public final MapperBuilderAssistant assistant;
    public final XNode mapper;
    public final String namespace;
    public final Map<String, LineNumberElement> lineMap = new HashMap<>();
    public final Map<String, String> resultMapMap = new HashMap<>();
    public final Map<String, String> typeAliasMap = new HashMap<>();

    public static XmlFileParser build(Configuration configuration, File file, GitRootInfo gitRootInfo) {
        try (FileInputStream fis = new FileInputStream(file)) {
            XmlFileParser xml = new XmlFileParser(fis, configuration, file, gitRootInfo);
            if (xml.mapper == null) {
                return null;
            }
            return xml;
        } catch (Exception e) {
            LOG.warn("XmlFileParser build fail: ", e);
            return null;
        }
    }

    public XmlFileParser(InputStream fis, Configuration configuration, File file, GitRootInfo gitRootInfo) {
        this.configuration = configuration;
        this.file = file;
        if (gitRootInfo != null) {
            this.gitRootInfo = gitRootInfo;
        } else {
            this.gitRootInfo = new GitRootInfo();
            this.gitRootInfo.setGitRoot(GitUtils.gitRoot(file));
        }
        assistant = new MapperBuilderAssistant(configuration, file.getAbsolutePath());
        XPathParser xPath;
        try {
            xPath = new XPathParser(fis, false, new Properties(), MAPPER_ENTITY_RESOLVER);
        } catch (Exception ignored) {
            // Ignore Exception when is not MyBatis *.xml and return null
            xPathParser = null;
            mapper = null;
            namespace = null;
            return;
        }
        xPathParser = xPath;
        mapper = xPathParser.evalNode("mapper");
        if (mapper == null) {
            namespace = null;
            return;
        }
        namespace = mapper.getStringAttribute("namespace");
        assistant.setCurrentNamespace(namespace);
        // line Num
        Document document;
        try {
            document = JdomUtils.builder().build(file);
        } catch (Exception e) {
            LOG.warn("Jdom build fail:\n{}\n", file.getAbsolutePath(), e);
            return;
        }
        Element rootElement = document.getRootElement();
        List<Element> children = rootElement.getChildren();
        for (Element child : children) {
            String name = child.getName();
            switch (name) {
                case "resultMap":
                    resultMapMap.put(child.getAttributeValue("id"), child.getAttributeValue("type"));
                    continue;
                case "typeAlias":
                    typeAliasMap.put(child.getAttributeValue("alias"), child.getAttributeValue("type"));
                    continue;
            }
            String id = child.getAttributeValue("id");
            if (id != null) {
                LineNumberElement lineNumberElement = (LineNumberElement) child;
                lineMap.put(id, lineNumberElement);
            }
        }
    }

    private void addLine(SqlInfo sqlInfo, String id) {
        LineNumberElement lineNumberElement = lineMap.get(id);
        if (lineNumberElement != null) {
            sqlInfo.setStartLine(lineNumberElement.getStartLine());
            sqlInfo.setEndLine(lineNumberElement.getEndLine());
        }
        sqlInfo.setId(id);
        sqlInfo.setFile(file);
        sqlInfo.setGitRootInfo(gitRootInfo);
    }

    public void parserSqlFragments() {
        try {
            List<XNode> sqlList = mapper.evalNodes("sql");
            for (XNode xmlSQL : sqlList) {
                String id = null;
                try {
                    id = xmlSQL.getStringAttribute("id");
                    Map<String, XNode> sqlFragments = configuration.getSqlFragments();
                    sqlFragments.put(namespace + "." + id, xmlSQL);
                } catch (Exception e) {
                    SqlInfo sqlInfo = new SqlInfo();
                    try {
                        // line
                        addLine(sqlInfo, id);
                        sqlInfo.setSql(xmlSQL.getStringBody());
                        String msg = SqlInfoLog.msg("parserSqlFragments", sqlInfo, e, true);
                        LOG.warn(msg);
                    } catch (Exception el) {
                        LOG.warn("parserSqlFragments fail: ", e);
                        LOG.warn("parserSqlFragments msg fail: ", el);
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("parserSqlFragments fail:", e);
        }
    }

    public void parserSql(Function<SqlInfo, Boolean> fun) {
        List<XNode> sqlList;
        try {
            sqlList = mapper.evalNodes("select|update|insert|delete");
        } catch (Exception e) {
            String name = e.getClass().getName();
            String msg = e.getLocalizedMessage();
            String file = PathUtils.canonicalPath(this.file);
            LOG.warn("evalNodes fail: {}: {}\nfile:///{}", name, msg, file);
            return;
        }
        for (XNode xmlSQL : sqlList) {
            SqlInfo sqlInfo = new SqlInfo();
            Map<Object, Object> map = buildParamMap(xmlSQL, sqlInfo);
            if (map != null) {
                buildSql(xmlSQL, map, sqlInfo);
            }
            try {
                if (!fun.apply(sqlInfo)) {
                    break;
                }
            } catch (Exception e) {
                String msg = SqlInfoLog.msg("parserSql fun.accept", sqlInfo, e, true);
                LOG.warn(msg);
            }
        }
    }

    private Map<Object, Object> buildParamMap(XNode xmlSQL, SqlInfo sqlInfo) {
        try {
            String id = xmlSQL.getStringAttribute("id");
            // line
            addLine(sqlInfo, id);
            sqlInfo.setNamespace(namespace);
            sqlInfo.setType(xmlSQL.getName());
            sqlInfo.setParameterType(xmlSQL.getStringAttribute("parameterType"));

            String resultMap = xmlSQL.getStringAttribute("resultMap");
            if (resultMap != null) {
                sqlInfo.setResultMap(resultMap);
                sqlInfo.setResultType(resultMapMap.get(resultMap));
            } else {
                sqlInfo.setResultType(xmlSQL.getStringAttribute("resultType"));
            }
            String type = typeAliasMap.get(sqlInfo.getResultType());
            if (type != null) {
                sqlInfo.setResultType(type);
            }

            XMLIncludeTransformer transformer = new XMLIncludeTransformer(configuration, assistant);
            Node node = xmlSQL.getNode();
            transformer.applyIncludes(node);
            XmlChange.deleteSelectKey(node);
            Map<Object, Object> map = SqlParamBuilder.tagKey(xmlSQL);
            XmlChange.putKeyAndDeleteCallMethod(node, map);
            return map;
        } catch (Exception e) {
            String msg = SqlInfoLog.msg("buildParamMap", sqlInfo, e, true);
            LOG.warn(msg);
            return null;
        }
    }

    private void buildSql(XNode xmlSQL, Map<Object, Object> map, SqlInfo sqlInfo) {
        try {
            XMLScriptBuilder xml = new XMLScriptBuilder(configuration, xmlSQL);
            SqlSource source = xml.parseScriptNode();
            BoundSql boundSql = source.getBoundSql(map);
            String sql = boundSql.getSql();
            sql = sql.trim();
            if (!sql.isEmpty()) {
                sql = sql.replace("?", "''");
                sqlInfo.setSql(sql);
            }
        } catch (Exception e) {
            String msg = SqlInfoLog.msg("getBoundSql", sqlInfo, e, true);
            LOG.warn(msg);
        }
    }
}
