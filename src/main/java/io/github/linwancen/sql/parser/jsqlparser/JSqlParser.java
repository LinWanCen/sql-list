package io.github.linwancen.sql.parser.jsqlparser;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.jsqlparser.statement.StatementVisitor;
import io.github.linwancen.sql.parser.log.SqlInfoLog;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.regex.Pattern;

public class JSqlParser {
    private static final Logger LOG = LoggerFactory.getLogger(JSqlParser.class);
    public static final Pattern DELETE_PATTERN = Pattern.compile("(?i)((?:ur|sw)$" +
            "|BINARY" +
            "|storageDb[\\s\\S]*" +
            "|\n\n|\r\r|\r\n\r\n)");

    public static void parseSQL(SqlInfo sqlInfo) {
        parseSQL(sqlInfo, s -> DELETE_PATTERN.matcher(s).replaceAll(""));
    }

    public static void parseSQL(SqlInfo sqlInfo, Function<String, String> preParser) {
        String sql = sqlInfo.getSql();
        if (sql == null) {
            return;
        }
        sql = preParser.apply(sql);
        try {
            CCJSqlParser parser = new CCJSqlParser(sql);
            Statement statement = parser.Statement();
            StatementVisitor.accept(sqlInfo, statement);
        } catch (Exception e) {
            String msg = SqlInfoLog.msg("parseSQL", sqlInfo, e, false);
            LOG.warn(msg);
        }
    }
}
