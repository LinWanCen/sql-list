package io.github.linwancen.sql.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class SqlInfoWriter {

    public static void write(ExcelWriter excelWriter, List<SqlInfo> sqlInfo,
                             WriteSheet sql, WriteSheet table, WriteSheet column) {
        excelWriter.write(sqlInfo, sql);
        for (SqlInfo info : sqlInfo) {
            for (TreeMap<String, TableColumn> map : info.getColumnList()) {
                for (TableColumn v : map.values()) {
                    if (v.getTable() == null && info.getTableMap().size() == 1) {
                        String tableName = info.getTableMap().keySet().iterator().next();
                        v.setTable(tableName);
                    }
                    excelWriter.write(Collections.singleton(v), column);

                    TableColumn tableColumn = info.getTableMap().get(v.getTable());
                    if (tableColumn != null) {
                        tableColumn.getColumnUseTypeSet().add(v.getColumnUseType());
                        tableColumn.getColumnSet().add(v.getColumn());
                    }
                }
            }
            for (TableColumn v : info.getTableMap().values()) {
                excelWriter.write(Collections.singleton(v), table);
            }
        }
    }
}
