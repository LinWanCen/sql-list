package io.github.linwancen.sql.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.github.linwancen.sql.bean.Rel;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import io.github.linwancen.util.excel.ExcelUtils;
import io.github.linwancen.util.plantuml.PlantUML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class SqlInfoWriter {

    private static final Logger LOG = LoggerFactory.getLogger(SqlInfoWriter.class);


    private final ExcelWriter excelWriter;
    private final WriteSheet sql;
    private final WriteSheet table;
    private final WriteSheet column;
    private final WriteSheet rel;
    private final Set<List<String>> columnRel = new HashSet<>();

    public SqlInfoWriter(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
        sql = ExcelUtils.sheet(this.excelWriter, "sql", SqlInfo.class);
        table = ExcelUtils.sheet(excelWriter, "table", TableColumn.class);
        column = ExcelUtils.sheet(excelWriter, "column", TableColumn.class);
        rel = ExcelUtils.sheet(excelWriter, "rel", Rel.class);
    }

    public void write(List<SqlInfo> sqlInfo) {
        excelWriter.write(sqlInfo, sql);
        for (SqlInfo info : sqlInfo) {
            for (TreeMap<String, TableColumn> map : info.getColumnList()) {
                for (TableColumn v : map.values()) {
                    // column table null and one table use it
                    if (v.getTable() == null && info.getTableMap().size() == 1) {
                        String tableName = info.getTableMap().keySet().iterator().next();
                        v.setTable(tableName);
                    }
                    excelWriter.write(Collections.singleton(v), column);

                    if (v.getTable() != null) {
                        // table sheet useTypes and columns
                        TableColumn tableColumn = info.getTableMap().get(v.getTable());
                        if (tableColumn != null) {
                            tableColumn.getColumnUseTypeSet().add(v.getColumnUseType());
                            tableColumn.getColumnSet().add(v.getColumn());
                        }
                    }
                }
            }
            for (TableColumn v : info.getTableMap().values()) {
                excelWriter.write(Collections.singleton(v), table);
            }
            columnRel.addAll(info.getColumnRel());
        }
        StringBuilder builder = new StringBuilder(PlantUML.start());
        columnRel.stream()
                .sorted(Comparator.comparing(list -> list.get(0)))
                .forEach(list -> {
                    excelWriter.write(Collections.singleton(list), rel);
                    String left = list.get(0);
                    String right = list.get(1);
                    item(builder, left);
                    item(builder, right);
                    builder.append("\n").append(left).append(" --> ").append(right).append("\n");
                });
        builder.append(PlantUML.end());


        File file = excelWriter.writeContext().writeWorkbookHolder().getFile();
        String path = file.getAbsolutePath();
        String pathPrefix = path.substring(0, path.lastIndexOf("."));
        File plantumlFile = new File(pathPrefix + ".puml");
        try {
            Files.write(plantumlFile.toPath(), builder.toString().getBytes(StandardCharsets.UTF_8));
            LOG.info("PlantUML:\tfile:///{}.puml", pathPrefix.replace('\\', '/'));
        } catch (IOException e) {
            LOG.error("PlantUML write fail: ", e);
        }
    }

    private static void item(StringBuilder builder, String left) {
        int end = left.lastIndexOf(".");
        if (end == -1) {
            return;
        }
        builder.append("\ncomponent ").append(left, 0, end).append(" {");
        builder.append("\ncomponent ").append(left).append(" as \"").append(left, end + 1, left.length()).append('"');
        builder.append("\n}");
    }
}
