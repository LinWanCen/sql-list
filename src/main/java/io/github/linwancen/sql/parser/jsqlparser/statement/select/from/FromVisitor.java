package io.github.linwancen.sql.parser.jsqlparser.statement.select.from;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.jsqlparser.AddUtils;
import io.github.linwancen.sql.parser.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;

public class FromVisitor extends FromItemVisitorAdapter {
    private final SqlInfo sqlInfo;
    private final Alias selectAlias;

    private FromVisitor(SqlInfo sqlInfo, Alias selectAlias) {
        this.sqlInfo = sqlInfo;
        this.selectAlias = selectAlias;
    }

    public static void accept(SqlInfo sqlInfo, FromItem from) {
        acceptAlias(sqlInfo, from, null);
    }

    public static void acceptAlias(SqlInfo sqlInfo, FromItem from, Alias selectAlias) {
        if (from != null) {
            from.accept(new FromVisitor(sqlInfo, selectAlias));
        }
    }

    @Override
    public void visit(Table table) {
        AddUtils.addTableAlias(sqlInfo, table, selectAlias);
    }

    @Override
    public void visit(ParenthesedSelect selectBody) {
        SelectVisitor.acceptAlias(sqlInfo, selectBody.getSelect(), selectBody.getAlias());
    }
}
