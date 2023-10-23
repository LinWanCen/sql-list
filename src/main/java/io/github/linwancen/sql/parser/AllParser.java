package io.github.linwancen.sql.parser;

import com.alibaba.druid.DbType;
import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.util.java.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllParser {
    private static final Logger LOG = LoggerFactory.getLogger(AllParser.class);

    public static List<SqlInfo> parse(Collection<File> files, DbType dbType, GitRootInfo gitRootInfo) {
        List<SqlInfo> sqlInfoList = new ArrayList<>();
        long t1 = System.currentTimeMillis();

        MapperParser.parseFiles(files, sqlInfoList, gitRootInfo);
        long t2 = System.currentTimeMillis();
        LOG.info("sql size {}", sqlInfoList.size());
        LOG.info("MapperParser use {}", TimeUtils.useTime(t2 - t1));

        sqlInfoList.parallelStream().forEach(sqlInfo -> SqlParser.parseSQL(sqlInfo, dbType));
        long t3 = System.currentTimeMillis();
        LOG.info("SqlParser use {}", TimeUtils.useTime(t3 - t2));

        sqlInfoList.parallelStream().forEach(GitParser::parseLast);
        long t4 = System.currentTimeMillis();
        LOG.info("GitParser use {}", TimeUtils.useTime(t4 - t3));

        return sqlInfoList;
    }
}
