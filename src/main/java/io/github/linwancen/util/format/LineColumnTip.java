package io.github.linwancen.util.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineColumnTip {
    public static final Pattern LINE_COLUMN_PATTERN = Pattern.compile("line (\\d++), column (\\d++)");
    public static final Pattern LINE_PATTERN = Pattern.compile("[\r\n]|\r\n");

    public static String parseMsg(String s, String msg) {
        return parsePatternMsg(s, msg, LINE_COLUMN_PATTERN);
    }

    public static String parsePatternMsg(String s, String msg, Pattern pattern) {
        Matcher m = pattern.matcher(msg);
        if (!m.find()) {
            return s;
        }
        int line = Integer.parseInt(m.group(1));
        int column = Integer.parseInt(m.group(2));
        return LineColumnTip.parseLineColumnMsg(s, line, column);
    }

    public static String parseLineColumnMsg(String s, int line, int column) {
        if (s == null || line <= 0 || column <= 0) {
            return s;
        }
        Matcher m = LINE_PATTERN.matcher(s);
        if (!m.find()) {
            return s;
        }
        String lineEnd = m.group();

        String[] split = LINE_PATTERN.split(s);
        if (split.length < line) {
            return s;
        }

        int tipLineIndex = line - 1;
        String tipLine = split[tipLineIndex];
        if (tipLine.length() < column) {
            return s;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line; i++) {
            sb.append(split[i]).append(lineEnd);
        }
        int tipColumnIndex = column - 1;
        for (int i = 0; i < tipColumnIndex; i++) {
            char c = tipLine.charAt(i);
            if (c == '\t') {
                sb.append('\t');
            } else if (c > 0xff) {
                sb.append("  ");
            } else {
                sb.append(' ');
            }
        }
        sb.append('^').append(lineEnd);
        int endIndex = split.length - 1;
        for (int i = line; i < endIndex; i++) {
            sb.append(split[i]).append(lineEnd);
        }
        if (line <= endIndex) {
            sb.append(split[endIndex]);
        }
        return sb.toString();
    }
}
