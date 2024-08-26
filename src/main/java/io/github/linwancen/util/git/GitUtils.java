package io.github.linwancen.util.git;

import io.github.linwancen.util.CmdUtils;
import io.github.linwancen.util.java.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitUtils {
    private static final Logger LOG = LoggerFactory.getLogger(GitUtils.class);

    public static File gitRoot(File file) {
        File parentFile = file;
        while ((parentFile = parentFile.getParentFile()) != null) {
            if (new File(parentFile, ".git").exists()) {
                return parentFile;
            }
        }
        return null;
    }

    /**
     * 1.hash 文件路径 (2.作者 3.yyyy-MM-dd HH:mm:ss
     */
    public static final Pattern BLAME_PATTERN = Pattern.compile(
            "(\\w++) .*\\(" +
                    "(.*?) ++" +
                    "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})"
    );

    public static void utf8() {
        CmdUtils.execOut("git config --global core.quotepath false", null, 1000, null);
    }

    public static void blame(File gitRoot, File file, int start, int endLine,
                             Consumer<String> func) {
        if (gitRoot == null || start == 0 || endLine == 0) {
            return;
        }
        String path = file.getAbsolutePath();
        String gitPath = gitRoot.getAbsolutePath();
        String subPath = path.substring(gitPath.length() + 1).replace('\\', '/');
        String cmd = "git blame -w -L" + start + "," + endLine + " " + subPath;
        CmdUtils.exec(cmd, gitRoot, 60 * 1000, func, errMsg -> {
            if (!errMsg.isEmpty() && !errMsg.contains("no such ref")) {
                LOG.warn("cmd err:{}\n{}", errMsg, cmd);
            }
        });
    }

    public static List<File> diff(String startToEnd, String gitPath, File gitRoot) {
        String cmd = "git diff --name-only " + startToEnd;
        StringBuilder sb = new StringBuilder();
        CmdUtils.execOut(cmd, gitRoot, 60 * 1000, sb::append);
        return Arrays.stream(FileUtils.split(sb.toString()))
                .filter(s -> !s.isEmpty())
                .map(s -> new File(gitPath, s))
                .collect(Collectors.toList());
    }

    public static Date gitDate(String hashOrTagOrBranch, File gitRoot) {
        return toDate(gitDateStr(hashOrTagOrBranch, gitRoot));
    }

    public static String gitDateStr(String hashOrTagOrBranch, File gitRoot) {
        String cmd = "git log -1 --pretty=format:\"%ci\" " + hashOrTagOrBranch;
        StringBuilder sb = new StringBuilder();
        CmdUtils.execOut(cmd, gitRoot, 60 * 1000, out -> sb.append(out).append("\n"));
        return sb.toString().trim();
    }

    public static Date toDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            LOG.warn("{} is not yyyy-MM-dd HH:mm:ss", dateStr, e);
            return null;
        }
    }
}
