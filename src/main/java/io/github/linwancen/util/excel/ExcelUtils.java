package io.github.linwancen.util.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class ExcelUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);
    public static void write(String outName, Consumer<ExcelWriter> fun) {
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HH.mm.ss");
        File outFile = new File(outName + sdf.format(new Date()) + ".xlsx");
        PathUtils.mkdir(outFile.getParentFile());
        try (ExcelWriter writer = EasyExcelFactory.write(outFile).build()) {
            fun.accept(writer);
        }
        String path = outFile.getAbsolutePath().replace('\\', '/');
        LOG.info("Excel：\tfile:///{}", path);
    }

    public static <T> WriteSheet sheet(String sheetName, Class<T> clazz) {
        return EasyExcelFactory
                .writerSheet(sheetName)
                .head(clazz)
                .registerWriteHandler(new FreezeAndFilter())
                .build();
    }
}
