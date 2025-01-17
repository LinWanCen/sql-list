package io.github.linwancen.sql.parser;

import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.util.git.GitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

public class GitParser {
    private static final Logger LOG = LoggerFactory.getLogger(GitParser.class);

    public static void parseLast(SqlInfo sqlInfo) {
        GitRootInfo gitRootInfo = sqlInfo.getGitRootInfo();
        if (gitRootInfo == null) {
            return;
        }
        File gitRoot = gitRootInfo.getGitRoot();
        if (sqlInfo.getLastDate() != null) {
            return;
        }
        GitUtils.blame(gitRoot, sqlInfo.getFile(), sqlInfo.getStartLine(), sqlInfo.getEndLine(), out -> {
            try {
                Matcher m = GitUtils.BLAME_PATTERN.matcher(out);
                while (m.find()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(m.group(3));
                    if (sqlInfo.getLastDate() == null || sqlInfo.getLastDate().before(date)) {
                        sqlInfo.setLastDate(date);
                        sqlInfo.setLastAuthor(m.group(2));
                    }
                    if (sqlInfo.getInDate() == null) {
                        Date startDate = gitRootInfo.getStartDate();
                        Date endDate = gitRootInfo.getEndDate();
                        if (!(startDate == null && endDate == null)) {
                            boolean afterStart = startDate == null || (date.after(startDate) || date.equals(startDate));
                            boolean beforeEndDate = endDate == null || (date.before(endDate) || date.equals(endDate));
                            if (afterStart && beforeEndDate) {
                                sqlInfo.setInDate(date);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.warn("blame fail:\n{}\n", out, e);
            }
        });
    }

}
