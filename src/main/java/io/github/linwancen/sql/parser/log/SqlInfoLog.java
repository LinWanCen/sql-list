package io.github.linwancen.sql.parser.log;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.GitParser;
import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.format.LineColumnTip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlInfoLog {
    private static final Logger LOG = LoggerFactory.getLogger(SqlInfoLog.class);
    private static final Map<String, String> ignoreIdMap = new ConcurrentHashMap<>();

    public static void loadAndAddIgnore(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        ignoreIdMap.clear();
        files.parallelStream().forEach(file -> {
            try {
                List<String> list = Files.readAllLines(file.toPath());
                for (String s : list) {
                    ignoreIdMap.put(s, "");
                }
            } catch (Exception e) {
                LOG.error("loadAndAddIgnore fail: ", e);
            }
        });
    }

    /**
     * gitAuthor name fail: ExceptionClassSimpleName: LocalizedMessage.delete("Was expecting..."）
     * <br>.(fileName:startLine) id file:///filePath
     * <br>sql
     * <br>^
     */
    public static String msg(String name, SqlInfo sqlInfo, Exception e, boolean inXml) {
        if (ignoreIdMap.containsKey(sqlInfo.getFullId())) {
            return name + " ignore fail: " + sqlInfo.getFullId()
                    + "\n file:///" + PathUtils.canonicalPath(sqlInfo.getFile()) + "\n";
        }
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        if (inXml) {
            sqlInfo.setXmlErr(stackTrace);
        } else {
            sqlInfo.setSqlErr(stackTrace);
        }
        try {
            String msg = e.getLocalizedMessage();
            if (msg == null) {
                return stackTrace;
            }
            StringBuilder sb = new StringBuilder(name).append(" fail:\n");
            msg = addExceptionMsg(sqlInfo, e, inXml, sb, msg);
            File file = sqlInfo.getFile();
            if (file != null) {
                GitParser.parseLast(sqlInfo);
                sb.append(sqlInfo.getLastAuthor()).append(' ');
                sb.append(".(").append(file.getName()).append(':').append(sqlInfo.getStartLine()).append(") ");
                sb.append(sqlInfo.getId()).append("\n");
                sb.append("file:///").append(PathUtils.canonicalPath(file)).append("\n");
            }

            String sql = sqlInfo.getSql();
            if (sql == null) {
                return sb.toString();
            }
            sql = LineColumnTip.parseMsg(sql, msg);
            sqlInfo.setSql(sql);
            sb.append('\n').append(sql).append("\n");
            return sb.toString();
        } catch (RuntimeException re) {
            LOG.warn("SqlInfoLog.msg() Exception: ", re);
            return stackTrace;
        }
    }

    private static String addExceptionMsg(SqlInfo sqlInfo, Exception e, boolean inXml, StringBuilder sb, String msg) {
        sb.append(e.getClass().getSimpleName()).append(": ");
        int i = msg.indexOf("\nWas expecting");
        if (i > 0) {
            msg = msg.substring(0, i).replace("\n", "");
        }
        if (inXml) {
            sqlInfo.setXmlErr(msg);
        } else {
            sqlInfo.setSqlErr(msg);
        }
        sb.append(msg).append('\n');
        return msg;
    }
}
