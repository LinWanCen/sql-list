package io.github.linwancen.sql.parser.jsqlparser.statement.select.from;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.jsqlparser.statement.WhereVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Join;

import java.util.List;

public class JoinVisitor {

    public static void accept(SqlInfo sqlInfo, List<Join> joins) {
        if (joins != null) {
            for (Join join : joins) {
                FromVisitor.accept(sqlInfo, join.getFromItem());
                WhereVisitor whereVisitor = new WhereVisitor(sqlInfo, "on");
                for (Expression expression : join.getOnExpressions()) {
                    whereVisitor.accept(expression);
                }
            }
        }
    }
}
