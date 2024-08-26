package io.github.linwancen.sql.parser.log;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.GitParser;
import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.format.LineColumnTip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SqlInfoLog {
    private static final Logger LOG = LoggerFactory.getLogger(SqlInfoLog.class);

    /**
     * gitAuthor name fail: ExceptionClassSimpleName: LocalizedMessage.delete("Was expecting..."）
     * <br>.(fileName:startLine) id file:///filePath
     * <br>sql
     * <br>^
     */
    public static String msg(String name, SqlInfo sqlInfo, Exception e, boolean inXml) {
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
            StringBuilder sb = new StringBuilder(name).append(" fail: ");
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
            GitParser.parseLast(sqlInfo);
            sb.append(sqlInfo.getLastAuthor()).append(' ');
            sb.append(".(").append(sqlInfo.getFile().getName()).append(':').append(sqlInfo.getStartLine()).append(") ");
            sb.append(sqlInfo.getId());
            sb.append(" file:///").append(PathUtils.canonicalPath(sqlInfo.getFile()));

            String sql = sqlInfo.getSql();
            if (sql == null) {
                return sb.toString();
            }
            sql = LineColumnTip.parseMsg(sql, msg);
            sqlInfo.setSql(sql);
            sb.append('\n').append(sql);
            return sb.toString();
        } catch (RuntimeException re) {
            LOG.warn("SqlInfoLog.msg() Exception: ", re);
            return stackTrace;
        }
    }
}
