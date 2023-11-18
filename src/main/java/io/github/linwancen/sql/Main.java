package io.github.linwancen.sql;

import com.alibaba.druid.DbType;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.AllParser;
import io.github.linwancen.util.excel.ExcelUtils;
import io.github.linwancen.util.git.GitUtils;
import io.github.linwancen.util.java.EnvUtils;
import io.github.linwancen.util.java.FileUtils;
import io.github.linwancen.util.java.JFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        GitUtils.utf8();
        String name = EnvUtils.get("dbType", null, "mysql");
        DbType dbType = DbType.of(name);

        ExcelUtils.write("out/sql-list", excelWriter -> {
            WriteSheet sql = ExcelUtils.sheet("sql", SqlInfo.class);
            forArgs(args, dbType, sqlInfo -> excelWriter.write(sqlInfo, sql));

            WriteSheet cmd = EasyExcelFactory.writerSheet("cmd").build();
            List<String> line = Collections.singletonList(String.join(" ", args));
            List<List<String>> data = Collections.singletonList(line);
            excelWriter.write(data, cmd);
        });
    }

    private static void forArgs(String[] args, DbType dbType, Consumer<List<SqlInfo>> fun) {
        if (args.length == 0) {
            File[] files = JFileUtils.chooser();
            if (files.length == 0) {
                LOG.warn("未选择文件！");
            }
            List<SqlInfo> sqlInfoList = AllParser.parse(Arrays.asList(files), dbType, null);
            fun.accept(sqlInfoList);
            return;
        }
        if (args.length == 1) {
            args = FileUtils.split(args[0]);
        }
        String first = args[0];
        if (first.contains("..")) {
            DiffGit.gitBetween(args, dbType, fun);
        } else if ("diff".equals(first)) {
            DiffSql.diff(args, dbType, fun);
        } else {
            List<File> files = Arrays.stream(args).parallel()
                    .map(File::new)
                    .collect(Collectors.toList());
            List<SqlInfo> sqlInfoList = AllParser.parse(files, dbType, null);
            fun.accept(sqlInfoList);
        }
    }
}