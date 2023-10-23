package io.github.linwancen.sql.parser;

import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.git.GitUtils;
import io.github.linwancen.util.java.MultiFileUtils;
import io.github.linwancen.util.xml.JdomUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.contrib.input.LineNumberElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class MapperParser {
    private static final Logger LOG = LoggerFactory.getLogger(MapperParser.class);

    public static final Pattern SQL_TYPE_PATTERN = Pattern.compile("select|update|insert|delete");

    public static void parseFiles(Collection<File> files, List<SqlInfo> sqlInfoList, GitRootInfo gitRootInfo) {
        files.parallelStream().forEach(file -> {
            GitRootInfo rootInfo;
            if (gitRootInfo != null) {
                rootInfo = gitRootInfo;
            } else {
                rootInfo = new GitRootInfo();
                rootInfo.setGitRoot(GitUtils.gitRoot(file));
            }
            MultiFileUtils.walk(file, f -> {
                String path = PathUtils.canonicalPath(f);
                if (path.contains("/target/")) {
                    return;
                }
                if (path.endsWith("Mapper.xml")) {
                    MapperParser.parseFile(f, e -> {
                        e.setGitRootInfo(rootInfo);
                        sqlInfoList.add(e);
                    });
                }
            });
        });
    }

    public static void parseFile(File file, Consumer<SqlInfo> fun) {
        Document document;
        try {
            document = JdomUtils.builder().build(file);
        } catch (Exception e) {
            LOG.warn("Jdom build fail:\n{}\n", file.getAbsolutePath(), e);
            return;
        }
        Element rootElement = document.getRootElement();
        List<Element> children = rootElement.getChildren();
        String namespace = rootElement.getAttributeValue("namespace");
        for (Element child : children) {
            String type = child.getName();
            if (!SQL_TYPE_PATTERN.matcher(type).find()) {
                continue;
            }
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setFile(file);
            sqlInfo.setNamespace(namespace);
            sqlInfo.setType(type);
            sqlInfo.setId(child.getAttributeValue("id"));
            sqlInfo.setParameterType(child.getAttributeValue("parameterType"));
            sqlInfo.setResultMap(child.getAttributeValue("resultMap"));
            StringBuilder sql = new StringBuilder(child.getTextNormalize());
            // support <if> etc.
            List<Element> subs = child.getChildren();
            if (!subs.isEmpty()) {
                for (Element sub : subs) {
                    sql.append(" ").append(sub.getTextNormalize());
                }
            }
            sqlInfo.setSql(sql.toString());
            LineNumberElement lineNumberElement = (LineNumberElement) child;
            sqlInfo.setStartLine(lineNumberElement.getStartLine());
            sqlInfo.setEndLine(lineNumberElement.getEndLine());
            fun.accept(sqlInfo);
        }
    }
}
