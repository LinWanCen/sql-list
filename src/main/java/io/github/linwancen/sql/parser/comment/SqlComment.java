package io.github.linwancen.sql.parser.comment;

import io.github.linwancen.sql.bean.SqlInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class SqlComment {
    private static final Logger LOG = LoggerFactory.getLogger(SqlComment.class);
    public static final Pattern TAB_PATTERN = Pattern.compile("\t");
    public static final Pattern HYPHEN_PATTERN = Pattern.compile(" - ");
    public static final Pattern SPACE_PATTERN = Pattern.compile(" ");
    public static final Pattern VERTICAL_PATTERN = Pattern.compile("\\|");
    public static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public final Map<String, String> tableMap = new ConcurrentHashMap<>();
    public final Map<String, Map<String, String>> columnMap = new ConcurrentHashMap<>();

    public static SqlComment of(List<File> tableCommentFiles, List<File> columnCommentFiles) {
        SqlComment sqlComment = new SqlComment();
        if (tableCommentFiles != null) {
            tableCommentFiles.parallelStream().forEach(file -> {
                try {
                    List<String> list = Files.readAllLines(file.toPath());
                    for (String s : list) {
                        String[] split1 = TAB_PATTERN.split(s);
                        if (split1.length < 2) {
                            continue;
                        }
                        String tableName = split1[0];
                        String tableComment = split1[1];
                        String[] split2 = HYPHEN_PATTERN.split(tableComment);
                        if (split2.length > 2) {
                            tableComment = split2[0];
                        }
                        sqlComment.tableMap.put(tableName, tableComment);
                    }
                } catch (Exception e) {
                    LOG.error("loadAndAddIgnore fail: ", e);
                }
            });
        }
        if (columnCommentFiles != null) {
            columnCommentFiles.parallelStream().forEach(file -> {
                try {
                    List<String> list = Files.readAllLines(file.toPath());
                    for (String s : list) {
                        String[] split1 = TAB_PATTERN.split(s);
                        if (split1.length < 2) {
                            continue;
                        }
                        String columnName = split1[0];
                        String columnComment = split1[1];
                        String[] split2 = SPACE_PATTERN.split(columnComment);
                        if (split2.length > 2) {
                            columnComment = split2[0];
                        }
                        String tableName = split1[2];
                        sqlComment.columnMap.computeIfAbsent(tableName, k -> new ConcurrentHashMap<>())
                                .put(columnName, columnComment);
                    }
                } catch (Exception e) {
                    LOG.error("loadAndAddIgnore fail: ", e);
                }
            });
        }
        return sqlComment;
    }

    public void add(SqlInfo sqlInfo) {
        if (sqlInfo.getTableMap().size() == 1) {
            sqlInfo.setTableComment(tableMap.get(sqlInfo.getTable()));
        }
        sqlInfo.getTableMap().values().parallelStream().forEach(m -> m.setTableComment(tableMap.get(m.getTable())));
        // map key is alias.column
        sqlInfo.getColumnList().parallelStream().forEach(map -> map.values().parallelStream().forEach(c -> {
            String table = c.getTable();
            if (table == null) {
                return;
            }
            String column = c.getColumn();
            c.setTableComment(tableMap.get(table));
            Map<String, String> cMap = columnMap.get(table);
            if (cMap != null) {
                c.setColumnComment(cMap.get(column));
            }
        }));
        sqlInfo.getColumnRel().parallelStream().forEach(rel -> {
            rel.setTableCommentL(tableMap.get(rel.getTableL()));
            rel.setTableCommentR(tableMap.get(rel.getTableR()));
            Map<String, String> lMap = columnMap.get(rel.getTableL());
            if (lMap != null) {
                rel.setColumnCommentL(lMap.get(rel.getColumnL()));
            }
            Map<String, String> rMap = columnMap.get(rel.getTableR());
            if (rMap != null) {
                rel.setColumnCommentR(rMap.get(rel.getColumnR()));
            }
        });
    }
}
