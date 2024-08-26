package io.github.linwancen.sql.parser.jsqlparser;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

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
        String tableName;
        String columnName = column.getColumnName();
        Table table = column.getTable();
        if (table == null) {
            tableName = null;
        } else {
            String aliasName = table.getName();
            String mapName = aliasName == null ? null : sqlInfo.getAliasMap().get(aliasName);
            tableName = mapName == null ? aliasName : mapName;
        }
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
}
