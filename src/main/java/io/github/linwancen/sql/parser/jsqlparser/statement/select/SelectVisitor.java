package io.github.linwancen.sql.parser.jsqlparser.statement.select;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.jsqlparser.statement.select.from.FromVisitor;
import io.github.linwancen.sql.parser.jsqlparser.statement.WhereVisitor;
import io.github.linwancen.sql.parser.jsqlparser.statement.select.from.JoinVisitor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

import java.util.List;

public class SelectVisitor extends SelectVisitorAdapter {
    private final SqlInfo sqlInfo;
    private final Alias selectAlias;

    private SelectVisitor(SqlInfo sqlInfo, Alias selectAlias) {
        this.sqlInfo = sqlInfo;
        this.selectAlias = selectAlias;
    }

    public static void accept(SqlInfo sqlInfo, Select select) {
        acceptAlias(sqlInfo, select, null);
    }

    public static void acceptWithItem(SqlInfo sqlInfo, WithItem withItem) {
        acceptAlias(sqlInfo, withItem.getSelect(), withItem.getAlias());
    }

    public static void acceptAlias(SqlInfo sqlInfo, Select select, Alias selectAlias) {
        if (select != null) {
            select.accept(new SelectVisitor(sqlInfo, selectAlias));
        }
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        List<WithItem> withItemsList = plainSelect.getWithItemsList();
        if (withItemsList != null) {
            for (WithItem withItem : withItemsList) {
                SelectVisitor.accept(sqlInfo, withItem);
            }
        }
        FromVisitor.acceptAlias(sqlInfo, plainSelect.getFromItem(), selectAlias);
        JoinVisitor.accept(sqlInfo, plainSelect.getJoins());
        new WhereVisitor(sqlInfo, "where").accept(plainSelect.getWhere());
        GroupByElement groupBy = plainSelect.getGroupBy();
        if (groupBy != null) {
            new WhereVisitor(sqlInfo, "group").accept(groupBy.getGroupByExpressionList());
        }
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        if (orderByElements != null) {
            WhereVisitor whereVisitor = new WhereVisitor(sqlInfo, "order");
            for (OrderByElement orderByElement : orderByElements) {
                whereVisitor.accept(orderByElement.getExpression());
            }
        }
    }

    @Override
    public void visit(SetOperationList setOpList) {
        List<Select> selects = setOpList.getSelects();
        for (Select select : selects) {
            select.accept(this);
        }
    }
}
