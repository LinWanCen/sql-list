package io.github.linwancen.sql.parser.mybatis;

import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.java.MultiFileUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class MapperFileFinder {

    public static void findMapperFileList(Collection<File> files,
                                                List<File> mapperFileList,
                                                List<File> ignoreFileList) {
        files.parallelStream().forEach(file -> {
            MultiFileUtils.walk(file, f -> {
                String path = PathUtils.canonicalPath(f);
                if (path.contains("/target/") || path.contains("/build/")) {
                    return;
                }
                if (path.endsWith(".xml")) {
                    mapperFileList.add(f);
                }
                if (path.endsWith("ignore.sql-list.txt")) {
                    ignoreFileList.add(f);
                }
            });
        });
    }
}
