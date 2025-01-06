package io.github.linwancen.sql.parser.jsqlparser;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import io.github.linwancen.sql.parser.jsqlparser.statement.StatementVisitor;
import io.github.linwancen.sql.parser.log.SqlInfoLog;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;
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
            oneTableColumnAndTableMapColumns(sqlInfo);
        } catch (Exception e) {
            if ("insert".equals(sqlInfo.getType()) && e.getLocalizedMessage().contains("where")) {
                return;
            }
            String msg = SqlInfoLog.msg("parseSQL", sqlInfo, e, false);
            LOG.warn(msg);
        }
    }

    public static void oneTableColumnAndTableMapColumns(SqlInfo info) {
        String oneTableName = null;
        if (info.getTableMap().size() == 1) {
            oneTableName = info.getTableMap().keySet().iterator().next();
        }
        for (TreeMap<String, TableColumn> map : info.getColumnList()) {
            for (TableColumn v : map.values()) {
                // column table null and one table use it
                if (v.getTable() == null && oneTableName != null) {
                    v.setTable(oneTableName);
                }
                if (v.getTable() != null) {
                    // table sheet useTypes and columns
                    TableColumn tableColumn = info.getTableMap().get(v.getTable());
                    if (tableColumn != null) {
                        tableColumn.getColumnUseTypeSet().add(v.getColumnUseType());
                        tableColumn.getColumnSet().add(v.getColumn());
                    }
                }
            }
        }
    }
}
