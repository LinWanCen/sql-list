package io.github.linwancen.sql.parser.jsqlparser;

import io.github.linwancen.sql.bean.Rel;
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
        String tableName = tableName(sqlInfo, column);
        String columnName = column.getColumnName().replace("`", "");
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
        String tableL = tableName(sqlInfo, left);
        String tableR = tableName(sqlInfo, right);
        Rel rel = Rel.sortRelOrNull(tableL, left.getColumnName(), tableR, right.getColumnName());
        if (rel != null) {
            sqlInfo.getColumnRel().add(rel);
        }
    }

    public static String tableName(SqlInfo sqlInfo, Column column) {
        Table table = column.getTable();
        if (table == null) {
            return null;
        } else {
            String aliasName = table.getName();
            if (aliasName == null) {
                return null;
            }
            String mapName = sqlInfo.getAliasMap().get(aliasName);
            if (mapName != null) {
                return mapName;
            }
            return aliasName.replace("`", "");
        }
    }
}
