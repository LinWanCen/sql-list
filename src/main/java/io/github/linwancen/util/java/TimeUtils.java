package io.github.linwancen.util.java;

/**
 * 耗时格式化
 */
public class TimeUtils {

    public static String useTime(Long ms) {
        long s = ms / 1000;
        long m = s / 60;
        long h = m / 60;
        long msp = ms % 1000;
        long sp = s % 60;
        long mp = m % 60;
        String ht = h > 0 ? h + "h " : "";
        String mt = mp > 0 ? mp + "m " : "";
        String st = sp > 0 ? sp + "s " : "";
        String mst = msp > 0 ? msp + "ms " : "";
        return ht + mt + st + mst;
    }
}
