package io.github.linwancen.sql.parser.mybatis;

import io.github.linwancen.util.java.ProjectFileFinder;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MapperFileFinder {
    public static final String XML = ".xml";
    public static final String IGNORE = "ignore.sql-list.txt";
    public static final String TABLE_COMMENT = "table.xml.doc.tsv";
    public static final String COLUMN_COMMENT = "column.xml.doc.tsv";
    public static final String[] FILE_ENDS = {XML, IGNORE, TABLE_COMMENT, COLUMN_COMMENT};

    public static Map<String, List<File>> findMapperFileList(Collection<File> files) {
        return ProjectFileFinder.findFile(files, FILE_ENDS);
    }
}
