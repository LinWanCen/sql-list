package io.github.linwancen.util.java;

import io.github.linwancen.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFileFinder {

    public static Map<String, List<File>> findFile(Collection<File> files, String[] fileEnds) {
        Map<String, List<File>> map = new HashMap<>();
        files.parallelStream().forEach(file -> MultiFileUtils.walk(file, f -> {
            String path = PathUtils.canonicalPath(f);
            if (path.contains("/target/") || path.contains("/build/")) {
                return;
            }
            for (String fileEnd : fileEnds) {
                if (path.endsWith(fileEnd)) {
                    map.computeIfAbsent(fileEnd, k -> new ArrayList<>()).add(f);
                    break;
                }
            }
        }));
        return map;
    }
}
