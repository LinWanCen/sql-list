package io.github.linwancen.sql.parser.jsqlparser.statement;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.jsqlparser.AddUtils;
import io.github.linwancen.sql.parser.jsqlparser.statement.select.SelectVisitor;
import io.github.linwancen.sql.parser.jsqlparser.statement.select.from.JoinVisitor;
import net.sf.jsqlparser.statement.Block;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;

public class StatementVisitor extends StatementVisitorAdapter {
    private final SqlInfo sqlInfo;

    private StatementVisitor(SqlInfo sqlInfo) {this.sqlInfo = sqlInfo;}

    public static void accept(SqlInfo sqlInfo, Statement statement) {
        if (statement != null) {
            statement.accept(new StatementVisitor(sqlInfo));
        }
    }

    @Override
    public void visit(Select select) {
        SelectVisitor.accept(sqlInfo, select);
    }

    @Override
    public void visit(Delete delete) {
        AddUtils.addTable(sqlInfo, delete.getTable());
        JoinVisitor.accept(sqlInfo, delete.getJoins());
        new WhereVisitor(sqlInfo, "delete-where").accept(delete.getWhere());
    }

    @Override
    public void visit(Update update) {
        AddUtils.addTable(sqlInfo, update.getTable());
        JoinVisitor.accept(sqlInfo, update.getJoins());
        new WhereVisitor(sqlInfo, "update-where").accept(update.getWhere());
    }

    @Override
    public void visit(Insert insert) {
        AddUtils.addTable(sqlInfo, insert.getTable());
        SelectVisitor.accept(sqlInfo, insert.getSelect());
    }

    @Override
    public void visit(Upsert upsert) {
        AddUtils.addTable(sqlInfo, upsert.getTable());
        SelectVisitor.accept(sqlInfo, upsert.getSelect());
    }

    @Override
    public void visit(Block block) {
        Statements statements = block.getStatements();
        if (statements!=null) {
            for (Statement statement : statements) {
                statement.accept(this);
            }
        }
    }

    @Override
    public void visit(Drop drop) {
        AddUtils.addTable(sqlInfo, drop.getName());
    }

    @Override
    public void visit(Truncate truncate) {
        AddUtils.addTable(sqlInfo, truncate.getTable());
    }
}
