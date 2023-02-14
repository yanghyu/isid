package com.github.yanghyu.isid.common.core.util;



public class CharUtil {

    private static final int CN_MIN = "一".charAt(0); //\u4e00

    private static final int CN_MAX = "龥".charAt(0); //\u9fa5

    /**
     * 获取脱敏用字符类型
     * @param c 字符
     * @return  1:数字 2:汉字 3:其它
     */
    public static int charType(char c) {
        return isValidNumber(c) ? 1 : (isHanZi(c) ? 2 : 3);
    }

    public static boolean isHanZi(char c) {
        //        try {
        //            Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        //            return sc == Character.UnicodeScript.HAN;
        //        } catch (Exception e) {
        //            return false;
        //        }
        return c >= CN_MIN && c <= CN_MAX;
    }

    public static boolean isValidNumber(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isValidHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    public static boolean isValidMailChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                || (c == '_') || (c == '.') || (c == '-');
    }

    public static boolean isSpacerChar(char c) {
        return (c == '.') || (c == '-') || (c == ':');
    }

    public static boolean isEndChar(char c) {
        return (c == ',') || (c == '}') || (c == ')') || (c == ']');
    }

    public static String nStarString(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            s.append("*");
        }
        return s.toString();
    }

    public static String nCharString(int n, char replacement) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            s.append(replacement);
        }
        return s.toString();
    }

}
