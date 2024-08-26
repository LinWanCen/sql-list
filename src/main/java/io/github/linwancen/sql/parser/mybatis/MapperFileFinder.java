package io.github.linwancen.sql.parser.mybatis;

import io.github.linwancen.util.PathUtils;
import io.github.linwancen.util.java.MultiFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapperFileFinder {

    public static List<File> findMapperFileList(Collection<File> files) {
        List<File> mapperList = new ArrayList<>();
        files.parallelStream().forEach(file -> {
            MultiFileUtils.walk(file, f -> {
                String path = PathUtils.canonicalPath(f);
                if (path.contains("/target/") || path.contains("/build/")) {
                    return;
                }
                if (path.endsWith(".xml")) {
                    mapperList.add(f);
                }
            });
        });
        return mapperList;
    }
}
