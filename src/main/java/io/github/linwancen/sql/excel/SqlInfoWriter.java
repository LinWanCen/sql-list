package io.github.linwancen.sql.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.github.linwancen.sql.bean.Dto;
import io.github.linwancen.sql.bean.Rel;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import io.github.linwancen.util.excel.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class SqlInfoWriter extends SqlInfoOut{
    private static final Logger LOG = LoggerFactory.getLogger(SqlInfoWriter.class);

    private final ExcelWriter excelWriter;
    private final WriteSheet sql;
    private final WriteSheet table;
    private final WriteSheet column;
    private final WriteSheet rel;
    private final WriteSheet dto;

    public SqlInfoWriter(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
        sql = ExcelUtils.sheet(this.excelWriter, "sql", SqlInfo.class);
        table = ExcelUtils.sheet(excelWriter, "table", TableColumn.class);
        column = ExcelUtils.sheet(excelWriter, "column", TableColumn.class);
        rel = ExcelUtils.sheet(excelWriter, "rel", Rel.class);
        dto = ExcelUtils.sheet(excelWriter, "dto", Dto.class);
    }

    public void write(List<SqlInfo> sqlInfoList) {
        super.write(sqlInfoList);
        excelWriter.write(sqlInfoList, sql);
        excelWriter.write(tableList, table);
        excelWriter.write(columnList, column);
        excelWriter.write(columnRel, rel);
        excelWriter.write(dtoMap.values(), dto);
        File file = excelWriter.writeContext().writeWorkbookHolder().getFile();
        String path = file.getAbsolutePath();
        String pathPrefix = path.substring(0, path.lastIndexOf("."));
        write(pathPrefix, ".puml", puml, "PlantUML");
        write(pathPrefix, ".err.md", errMd, "Error");
    }

    public static void write(String pathPrefix, String ext, StringBuilder builder, String tip) {
        File errFile = new File(pathPrefix + ext);
        try {
            Files.write(errFile.toPath(), builder.toString().getBytes(StandardCharsets.UTF_8));
            LOG.info("{}:\tfile:///{}{}\n", tip, pathPrefix.replace('\\', '/'), ext);
        } catch (IOException e) {
            LOG.error("{} write fail: ", tip, e);
        }
    }
}
