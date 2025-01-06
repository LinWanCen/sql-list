package io.github.linwancen.sql.parser.mybatis.file.sql;

import org.apache.ibatis.parsing.XNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParamBuilder {
    public static final Pattern BOOLEAN_PATTERN = Pattern.compile("^[\\w.]++$");
    public static final Pattern EQUAL_PATTERN = Pattern.compile("([\\w.]++|'[^']++')(?:[(][)])? *([=!]=) *([\\w.]++|'[^']++')");
    public static final Pattern GELE_PATTERN = Pattern.compile("([\\w.]++|'[^']++') *[<>]=? *([\\w.]++|'[^']++')");
    public static final Pattern SPLIT_PATTERN = Pattern.compile("\\.");

    public static Map<Object, Object> tagKey(XNode xmlSQL) {
        HashMap<Object, Object> map = new HashMap<>();
        recursiveTagKey(xmlSQL, map);
        return map;
    }

    private static void recursiveTagKey(XNode xmlSQL, Map<Object, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        List<XNode> children = xmlSQL.getChildren();
        for (XNode child : children) {
            attrKV(map, child);
            recursiveTagKey(child, map);
        }
    }

    private static void attrKV(Map<Object, Object> map, XNode child) {
        // foreach
        String collection = child.getStringAttribute("collection");
        if (collection != null) {
            Object prop = putKV(map, collection, null);
            String item = child.getStringAttribute("item");
            if (item != null) {
                map.put(item, prop);
            }
            return;
        }
        // if
        String test = child.getStringAttribute("test");
        if (test != null) {
            boolean maybeBoolean = true;
            Matcher m = EQUAL_PATTERN.matcher(test);
            while (m.find()) {
                maybeBoolean = false;
                String k = m.group(1);
                String f = m.group(2);
                String v = m.group(3);
                putKV(map, k, boolAndNull(v, f));
                putKV(map, v, boolAndNull(k, f));
            }
            m = GELE_PATTERN.matcher(test);
            while (m.find()) {
                maybeBoolean = false;
                String k = m.group(1);
                String v = m.group(2);
                putKV(map, k, v);
                putKV(map, v, k);
            }
            if (maybeBoolean) {
                m = BOOLEAN_PATTERN.matcher(test);
                if (m.find()) {
                    String k = m.group();
                    putKV(map, k, true);
                }
            }
            return;
        }
        // bind
        String name = child.getStringAttribute("name");
        if (name != null) {
            putKV(map, name, "1");
        }
    }

    private static Object boolAndNull(String o, String f) {
        if ("null".equals(o) || "".equals(o)) {
            return "1";
        }
        if ("true".equals(o)) {
            return "==".equals(f);
        }
        if ("false".equals(o)) {
            return "!=".equals(f);
        }
        return o;
    }

    /**
     *
     * @return item list when collection
     */
    private static Object putKV(Map<Object, Object> map, String key, Object o) {
        if (key.startsWith("'")) {
            return null;
        }
        if (o != null) {
            String s = o.toString();
            if (s.startsWith("'")) {
                o = s.substring(1, s.length() - 1);
            }
        }
        String[] split = SPLIT_PATTERN.split(key);
        int lastIndex = split.length - 1;
        String name = split[lastIndex];
        if (split.length > 1 && ("name".equals(name) || "length".equals(name))) {
            o = name.length() == 4 ? null : new String[]{"1"};
            lastIndex--;
            name = split[lastIndex];
        }
        for (int i = 0; i < lastIndex; i++) {
            Object subMap = map.putIfAbsent(split[i], new HashMap<>());
            if (subMap instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> subList = (List<Object>) subMap;
                subMap = subList.get(0);
                if (!(subMap instanceof Map)) {
                    subMap = new HashMap<>();
                    subList.clear();
                    subList.add(subMap);
                }
            }
            if (!(subMap instanceof Map)) { // TODO 注释
                subMap = new HashMap<>();
                map.put(split[i], subMap);
            }
            //noinspection unchecked
            map = (Map<Object, Object>) subMap;
        }
        // not collection
        if (o != null) {
            if (o instanceof String[]) {
                map.put(name, o);
            } else {
                map.putIfAbsent(name, o);
            }
            return null;
        }
        // collection
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        Object subMap = map.putIfAbsent(name, list);
        if (subMap instanceof Map || subMap instanceof Collection || subMap instanceof String[]) {
            return list;
        }
        map.put(name, list);
        return list;
    }
}
