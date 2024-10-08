package io.github.linwancen.sql;

import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.AllParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiffSql {
    private static final Logger LOG = LoggerFactory.getLogger(DiffSql.class);

    public static void diff(String[] args, Consumer<List<SqlInfo>> fun, boolean git) {
        if (args.length < 3) {
            return;
        }
        int i1 = args[1].lastIndexOf('.');
        int i2 = args[2].lastIndexOf('.');
        String dir1 = i1 > 0 ? args[1].substring(0, i1) : args[1];
        String dir2 = i2 > 0 ? args[2].substring(0, i2) : args[2];
        diffDir(new File(dir1), new File(dir2), git, fun);
    }

    public static void diffDir(File file1, File file2, boolean git, Consumer<List<SqlInfo>> fun) {
        List<SqlInfo> oldList = AllParser.parse(Collections.singletonList(file1), null, git);
        List<SqlInfo> newList = AllParser.parse(Collections.singletonList(file2), null, git);
        Map<String, SqlInfo> oldMap = toIdMap(oldList);
        // delete duplicate
        Map<String, SqlInfo> newMap = toIdMap(newList);
        List<SqlInfo> diff = newMap.entrySet().parallelStream()
                .filter(entry -> {
                    SqlInfo oldInfo = oldMap.get(entry.getKey());
                    return oldInfo == null || !oldInfo.getSql().equals(entry.getValue().getSql());
                })
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        LOG.info("oldMap:{} diff:{}", oldMap.size(), diff.size());
        fun.accept(diff);
    }

    private static Map<String, SqlInfo> toIdMap(List<SqlInfo> oldList) {
        return oldList.parallelStream().collect(Collectors.toMap(
                sqlInfo -> sqlInfo.getNamespace() + "." + sqlInfo.getId(),
                sqlInfo -> sqlInfo,
                (sqlInfo, sqlInfo2) -> {
                    if (!sqlInfo.getSql().equals(sqlInfo2.getSql())) {
                        LOG.warn("duplicate and not equals for {}\n{}\n{}",
                                sqlInfo.getNamespace() + "." + sqlInfo.getId(),
                                sqlInfo.getFile().getAbsolutePath(),
                                sqlInfo2.getFile().getAbsolutePath());
                    }
                    return sqlInfo2;
                }));
    }
}
