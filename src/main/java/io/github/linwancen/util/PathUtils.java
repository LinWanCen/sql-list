package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PathUtils {

    @SuppressWarnings("ConstantConditions")
    public static final String CLASS_PATH = PathUtils.class.getResource("/").getPath();
    private static final Logger LOG = LoggerFactory.getLogger(PathUtils.class);

    private PathUtils() {}

    public static String canonicalPath(File file) {
        try {
            return file.getCanonicalPath().replace('\\', '/');
        } catch (IOException e) {
            String path = file.getAbsolutePath().replace('\\', '/');
            LOG.warn("getCanonicalPath IOException, use AbsolutePath\tfile:///{}", path, e);
            return path;
        }
    }

    /**
     * 避免没有文件夹报错
     */
    public static void mkdir(File dir) {
        if (dir != null && !dir.exists() && dir.mkdirs()) {
            String path = PathUtils.canonicalPath(dir);
            LOG.debug("mkdir fail\tfile:///{}", path);
        }
    }

    public static String dirSpaceName(String dirFile) {
        int nameIndex = dirFile.lastIndexOf('/') + 1;
        String dir = dirFile.substring(0, nameIndex);
        String name = dirFile.substring(nameIndex);
        return dir + " " + name;
    }

    public static void deleteFile(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            String dirSpaceName = PathUtils.dirSpaceName(PathUtils.canonicalPath(file));
            LOG.warn("Files.delete IOException\tfile:///{}", dirSpaceName, e);
        }
    }
}
