package io.github.linwancen.sql.parser.jsqlparser.statement;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import io.github.linwancen.sql.parser.jsqlparser.AddUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.TreeMap;

public class WhereVisitor extends ExpressionVisitorAdapter {
    private final SqlInfo sqlInfo;
    private final String columnUseType;
    private final TreeMap<String, TableColumn> map = new TreeMap<>();

    public WhereVisitor(SqlInfo sqlInfo, String columnUseType) {
        this.sqlInfo = sqlInfo;
        this.columnUseType = columnUseType;
        sqlInfo.getColumnList().add(map);
    }

    public void accept(Expression where) {
        if (where != null) {
            where.accept(this);
        }
    }

    @Override
    public void visit(Column column) {
        AddUtils.addColumn(sqlInfo, column, map, columnUseType);
    }
}
