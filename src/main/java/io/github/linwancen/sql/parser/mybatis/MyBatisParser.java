package io.github.linwancen.sql.parser.mybatis;

import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.mybatis.file.XmlFileParser;
import org.apache.ibatis.session.Configuration;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyBatisParser {
    public final Configuration configuration = new Configuration();

    public void parser(List<File> mapperFileList, GitRootInfo gitRootInfo, List<SqlInfo> sqlInfoList) {
        List<XmlFileParser> list = mapperFileList.stream()
                .parallel()
                .map(file -> XmlFileParser.build(configuration, file, gitRootInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        list.parallelStream().forEach(XmlFileParser::parserSqlFragments);
        // list.add() not parallel
        list.forEach(e -> e.parserSql(sqlInfoList::add));
    }
}
