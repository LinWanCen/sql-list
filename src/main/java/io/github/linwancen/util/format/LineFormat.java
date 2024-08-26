package io.github.linwancen.util.format;

import java.util.regex.Pattern;

public class LineFormat {
    public static final Pattern ITEMS_PATTERN = Pattern.compile(",\\s++");

    public static String itemsOneLine(String s) {
        return ITEMS_PATTERN.matcher(s).replaceAll(", ");
    }

    public static final Pattern SPACE_LINE_PATTERN = Pattern.compile("\\s+([\r\n])");

    public static String deleteSpaceLine(String s) {
        return SPACE_LINE_PATTERN.matcher(s).replaceAll("$1");
    }
}
