package io.github.linwancen.util.xml;

import org.jdom2.contrib.input.LineNumberSAXHandler;
import org.jdom2.input.SAXBuilder;

public class JdomUtils {
    public static SAXBuilder builder() {
        SAXBuilder builder = new SAXBuilder();
        builder.setSAXHandlerFactory(LineNumberSAXHandler.SAXFACTORY);
        builder.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        builder.setFeature(
                "http://xml.org/sax/features/validation",false);
        return builder;
    }
}
