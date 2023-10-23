package io.github.linwancen.sql;

import com.alibaba.druid.DbType;
import io.github.linwancen.sql.bean.GitRootInfo;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.parser.AllParser;
import io.github.linwancen.util.git.GitUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DiffGit {
    public static void gitBetween(String[] args, DbType dbType, Consumer<List<SqlInfo>> fun) {
        String[] split = args[0].split("\\.\\.");
        if (split.length != 2) {
            return;
        }
        Arrays.stream(args).skip(1).parallel().forEach(s -> {
            File gitRoot = new File(s);
            GitRootInfo gitRootInfo = new GitRootInfo();
            gitRootInfo.setGitRoot(gitRoot);
            gitRootInfo.setStartDate(GitUtils.gitDate(split[0], gitRoot));
            gitRootInfo.setEndDate(GitUtils.gitDate(split[1], gitRoot));
            List<File> files = GitUtils.diff(args[0], s, gitRoot);
            List<SqlInfo> sqlInfoList = AllParser.parse(files, dbType, gitRootInfo);
            fun.accept(sqlInfoList);
        });
    }
}
