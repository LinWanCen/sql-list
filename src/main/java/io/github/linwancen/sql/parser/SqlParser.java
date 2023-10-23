package io.github.linwancen.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {
    private static final Logger LOG = LoggerFactory.getLogger(SqlParser.class);

    public static final Pattern SELECT_PATTERN = Pattern.compile("(?i)select.*?from ++([\\w.]*)( ++and)?");
    public static final Pattern IN_PATTERN = Pattern.compile("(?i) in( ++(?!\\()|$)");
    public static final Pattern WHERE_AND_PATTERN = Pattern.compile("(?i)where ++and");
    public static final Pattern AND_AND_PATTERN = Pattern.compile("(?i)and ++and");
    public static final Pattern UPDATE_PATTERN = Pattern.compile("(?i)update ([\\w.]*) ++(?!set)");
    public static final Pattern END_PATTERN = Pattern.compile(", *(\\)|where|,)");
    public static final Pattern START_PATTERN = Pattern.compile("\\( *,");
    public static final Pattern REPLACE_PATTERN = Pattern.compile("(?i)replace\\s++into|insert ++ignore ++into" +
            "|insert(?! ++into)");
    public static final Pattern TOKEN_PATTERN = Pattern.compile("(?i)\bSECOND\b");
    public static final Pattern FROM_PATTERN = Pattern.compile("(?i)from ++([\\w.]*)");

    public static void parseSQL(SqlInfo sqlInfo, DbType dbType) {
        try {
            String sql = sqlInfo.getSql();
            if ("select".equals(sqlInfo.getType())) {
                sql = SELECT_PATTERN.matcher(sql).replaceAll("select * from $1");
                sql = WHERE_AND_PATTERN.matcher(sql).replaceAll("where ");
                sql = AND_AND_PATTERN.matcher(sql).replaceAll("and ");
                sql = IN_PATTERN.matcher(sql).replaceAll(" in ('') ");
                sql = TOKEN_PATTERN.matcher(sql).replaceAll("");
            } else if ("update".equals(sqlInfo.getType())) {
                sql = UPDATE_PATTERN.matcher(sql).replaceAll("update $1 set 1=1");
                sql = END_PATTERN.matcher(sql).replaceAll("$1");
            } else if ("insert".equals(sqlInfo.getType())) {
                sql = START_PATTERN.matcher(sql).replaceAll("(");
                sql = END_PATTERN.matcher(sql).replaceAll("$1");
                // druid not support.
                sql = REPLACE_PATTERN.matcher(sql).replaceAll("insert into");
            } else if ("delete".equals(sqlInfo.getType())) {
                Matcher m = FROM_PATTERN.matcher(sql);
                if (m.find()) {
                    sqlInfo.setTable(m.group(1));
                }
                // druid not support.
                return;
            }
            SQLStatementParser parser = new SQLStatementParser(sql, dbType);
            SQLStatement statement = parser.parseStatement();
            if (statement instanceof SQLSelectStatement) {
                parseSelect(sqlInfo, ((SQLSelectStatement) statement));
            } else if (statement instanceof SQLUpdateStatement) {
                parseUpdate(sqlInfo, ((SQLUpdateStatement) statement));
            } else if (statement instanceof SQLInsertStatement) {
                parseInsert(sqlInfo, ((SQLInsertStatement) statement));
            } else if (statement instanceof SQLDeleteStatement) {
                parseDelete(sqlInfo, ((SQLDeleteStatement) statement));
            }
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null) {
                LOG.warn("parseSQL fail:\n{}\nfile:///{}\n",
                        sqlInfo.getSql(), PathUtils.canonicalPath(sqlInfo.getFile()), e);
            } else {
                LOG.warn("parseSQL fail:{}\n{}\nfile:///{}",
                        e.getLocalizedMessage(), sqlInfo.getSql(), PathUtils.canonicalPath(sqlInfo.getFile()));
            }
        }
    }

    private static void parseSelect(SqlInfo sqlInfo, SQLSelectStatement statement) {
        List<SQLObject> sqlObjects = statement.getChildren();
        List<String> columns = new ArrayList<>();
        for (SQLObject sqlObject : sqlObjects) {
            if (sqlObject instanceof SQLSelect) {
                SQLSelect sqlSelect = (SQLSelect) sqlObject;
                SQLSelectQueryBlock queryBlock = sqlSelect.getQueryBlock();
                if (queryBlock == null) {
                    continue;
                }
                SQLTableSource table = queryBlock.getFrom();
                if (sqlInfo.getTable() != null) {
                    sqlInfo.setTable(sqlInfo.getTable() + "," + table.toString());
                } else {
                    sqlInfo.setTable(table.toString());
                }
                parseWhere(columns, queryBlock.getWhere());
            }
        }
        sqlInfo.setWhere(String.join(",", columns));
    }

    private static void parseUpdate(SqlInfo sqlInfo, SQLUpdateStatement statement) {
        sqlInfo.setTable(statement.getTableName().getSimpleName());
        List<String> columns = new ArrayList<>();
        parseWhere(columns, statement.getWhere());
        sqlInfo.setWhere(String.join(",", columns));
    }

    private static void parseInsert(SqlInfo sqlInfo, SQLInsertStatement statement) {
        sqlInfo.setTable(statement.getTableName().getSimpleName());
    }

    private static void parseDelete(SqlInfo sqlInfo, SQLDeleteStatement statement) {
        sqlInfo.setTable(statement.getTableName().getSimpleName());
        List<String> columns = new ArrayList<>();
        parseWhere(columns, statement.getWhere());
        sqlInfo.setWhere(String.join(",", columns));
    }


    /**
     * 递归解析 where 中的字段
     */
    private static void parseWhere(List<String> columns, SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            parseLeftRight(columns, ((SQLBinaryOpExpr) sqlExpr));
        } else if (sqlExpr instanceof SQLIdentifierExpr) {
            columns.add(sqlExpr.toString());
        }
    }

    /**
     * 解析左右表达式
     */
    public static void parseLeftRight(List<String> column, SQLBinaryOpExpr opExpr) {
        SQLExpr left = opExpr.getLeft();
        parseWhere(column, left);
        SQLExpr right = opExpr.getRight();
        parseWhere(column, right);
    }
}
