package io.github.linwancen.sql.parser.mybatis.file.sql;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlChange {

    public static void deleteSelectKey(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if ("selectKey".equals(childNode.getNodeName())) {
                node.removeChild(childNode);
            }
        }
    }

    public static final Pattern PARAM_PATTERN = Pattern.compile("[$][{]([^}]++)}");

    public static void putKeyAndDeleteCallMethod(Node node, Map<Object, Object> map) {
        if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
            String text = node.getNodeValue();
            int i = text.indexOf("@");
            if (i > 0) {
                String newText = PARAM_PATTERN.matcher(text).replaceAll("1");
                node.setTextContent(newText);
            } else {
                Matcher m = PARAM_PATTERN.matcher(text);
                while (m.find()) {
                    String s = m.group(1);
                    map.put(s, s);
                }
            }
            return;
        }
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            putKeyAndDeleteCallMethod(childNodes.item(i), map);
        }
    }
}
