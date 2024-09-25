package io.github.linwancen.sql.parser.jsqlparser;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.Arrays;
import java.util.TreeMap;

public class AddUtils {
    private AddUtils() {}

    public static void addTable(SqlInfo sqlInfo, Table table) {
        addTableAlias(sqlInfo, table, null);
    }

    public static void addTableAlias(SqlInfo sqlInfo, Table table, Alias selectAlias) {
        if (table == null) {
            return;
        }
        String name = table.getName();
        String s = sqlInfo.getAliasMap().get(name);
        if (s != null) {
            name = s;
        } else {
            Alias alias = table.getAlias();
            if (alias != null) {
                sqlInfo.getAliasMap().put(alias.getName(), name);
            }
            if (selectAlias != null) {
                sqlInfo.getAliasMap().put(selectAlias.getName(), name);
            }
        }
        sqlInfo.getTableMap().computeIfAbsent(name, k -> {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setNamespace(sqlInfo.getNamespace());
            tableColumn.setId(sqlInfo.getId());
            tableColumn.setType(sqlInfo.getType());
            tableColumn.setTable(k);
            return tableColumn;
        });
    }

    public static void addColumn(SqlInfo sqlInfo, Column column, TreeMap<String, TableColumn> map,
                                 String columnUseType) {
        if (column == null) {
            return;
        }
        String tableName = tableName(sqlInfo, column);
        String columnName = column.getColumnName();
        String tableColumnName = tableName == null ? columnName : tableName + "." + columnName;
        map.computeIfAbsent(tableColumnName, k -> {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setNamespace(sqlInfo.getNamespace());
            tableColumn.setId(sqlInfo.getId());
            tableColumn.setType(sqlInfo.getType());
            tableColumn.setTable(tableName);
            tableColumn.setColumn(columnName);
            tableColumn.setColumnUseType(columnUseType);
            return tableColumn;
        });
    }

    public static void addRel(SqlInfo sqlInfo, Column left, Column right) {
        String tableNameL = tableName(sqlInfo, left);
        String tableNameR = tableName(sqlInfo, right);
        String columnNameL = left.getColumnName();
        String columnNameR = right.getColumnName();
        String tableColumnNameL = tableNameL == null ? columnNameL : tableNameL + "." + columnNameL;
        String tableColumnNameR = tableNameR == null ? columnNameR : tableNameR + "." + columnNameR;
        tableColumnNameL = tableColumnNameL.toLowerCase();
        tableColumnNameR = tableColumnNameR.toLowerCase();
        int compare = tableColumnNameL.compareTo(tableColumnNameR);
        if (compare < 0) {
            sqlInfo.getColumnRel().add(Arrays.asList(tableColumnNameL, tableColumnNameR));
        } else if (compare > 0) {
            sqlInfo.getColumnRel().add(Arrays.asList(tableColumnNameR, tableColumnNameL));
        }
    }

    private static String tableName(SqlInfo sqlInfo, Column column) {
        Table table = column.getTable();
        if (table == null) {
            return null;
        } else {
            String aliasName = table.getName();
            String mapName = aliasName == null ? null : sqlInfo.getAliasMap().get(aliasName);
            return mapName == null ? aliasName : mapName;
        }
    }
}
