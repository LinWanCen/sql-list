package io.github.linwancen.sql.parser;

import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.comment.SqlComment;
import io.github.linwancen.sql.parser.jsqlparser.JSqlParser;
import io.github.linwancen.sql.parser.log.SqlInfoLog;
import io.github.linwancen.sql.parser.mybatis.MapperFileFinder;
import io.github.linwancen.sql.parser.mybatis.MyBatisParser;
import io.github.linwancen.util.java.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AllParser {
    private static final Logger LOG = LoggerFactory.getLogger(AllParser.class);

    public static List<SqlInfo> parse(Collection<File> files, GitRootInfo gitRootInfo, boolean git) {
        List<SqlInfo> sqlInfoList = new ArrayList<>();
        long t1 = System.currentTimeMillis();

        Map<String, List<File>> map = MapperFileFinder.findMapperFileList(files);
        SqlInfoLog.loadAndAddIgnore(map.get(MapperFileFinder.IGNORE));
        new MyBatisParser().parser(map.get(MapperFileFinder.XML), gitRootInfo, sqlInfoList::add);
        long t2 = System.currentTimeMillis();
        LOG.info("sql size {}\n", sqlInfoList.size());
        LOG.info("MyBatisParser use {}\n", TimeUtils.useTime(t2 - t1));

        List<File> tableCommentFiles = map.get(MapperFileFinder.TABLE_COMMENT);
        List<File> columnCommentFiles = map.get(MapperFileFinder.COLUMN_COMMENT);
        SqlComment sqlComment = SqlComment.of(tableCommentFiles, columnCommentFiles);
        sqlInfoList.parallelStream().forEach(sqlInfo -> {
            JSqlParser.parseSQL(sqlInfo);
            sqlComment.add(sqlInfo);
        });
        long t3 = System.currentTimeMillis();
        LOG.info("SqlParser use {}\n", TimeUtils.useTime(t3 - t2));

        if (git) {
            sqlInfoList.parallelStream().forEach(GitParser::parseLast);
            long t4 = System.currentTimeMillis();
            LOG.info("GitParser use {}\n", TimeUtils.useTime(t4 - t3));
        }

        return sqlInfoList;
    }
}
