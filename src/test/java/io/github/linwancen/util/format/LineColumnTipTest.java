package io.github.linwancen.util.format;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @see LineColumnTip
 */
public class LineColumnTipTest {

    @Test
    public void parseLineColumnMsg() {
        String s = "你好\n\t世界\nHi";
        System.out.println("\n0, 1:\n" + LineColumnTip.parseLineColumnMsg(s, 0, 1));
        System.out.println("\n1, 0:\n" + LineColumnTip.parseLineColumnMsg(s, 1, 0));
        System.out.println("\n1, 1:\n" + LineColumnTip.parseLineColumnMsg(s, 1, 1));
        System.out.println("\n1, 2:\n" + LineColumnTip.parseLineColumnMsg(s, 1, 2));
        System.out.println("\n1, 3:\n" + LineColumnTip.parseLineColumnMsg(s, 1, 3));
        System.out.println("\n2, 1:\n" + LineColumnTip.parseLineColumnMsg(s, 2, 1));
        System.out.println("\n2, 2:\n" + LineColumnTip.parseLineColumnMsg(s, 2, 2));
        System.out.println("\n2, 3:\n" + LineColumnTip.parseLineColumnMsg(s, 2, 3));
        System.out.println("\n2, 4:\n" + LineColumnTip.parseLineColumnMsg(s, 2, 4));
        System.out.println("\n3, 1:\n" + LineColumnTip.parseLineColumnMsg(s, 3, 1));
        System.out.println("\n3, 2:\n" + LineColumnTip.parseLineColumnMsg(s, 3, 2));
        System.out.println("\n3, 3:\n" + LineColumnTip.parseLineColumnMsg(s, 3, 3));
        System.out.println("\n4, 1:\n" + LineColumnTip.parseLineColumnMsg(s, 4, 1));
    }
}