package io.github.linwancen.util.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MultiFileUtils {
    public static void walk(File file, Consumer<File> fun) {
        if (file.isDirectory()) {
            try (Stream<Path> walk = Files.walk(file.toPath())) {
                walk.parallel().forEach(path -> {
                    File f = path.toFile();
                    if (f.isFile()) {
                        fun.accept(f);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            fun.accept(file);
        }
    }
}
