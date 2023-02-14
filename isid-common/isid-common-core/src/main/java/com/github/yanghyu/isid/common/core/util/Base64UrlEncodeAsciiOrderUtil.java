package com.github.yanghyu.isid.common.core.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 将Base64UrlEncode的字符串转换为Ascii默认排序
 */
public class Base64UrlEncodeAsciiOrderUtil {

    private static final char[] toBase64URL = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };

    private static final char[] toBase64URLAsciiOrder = {
            '-',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '_',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final HashMap<Character, Character> map = new HashMap<>();

    static {
        for (int i = 0; i < 64; i++) {
            map.put(toBase64URL[i], toBase64URLAsciiOrder[i]);
        }
    }

    public static String convert(String base64UrlEncodeString) {
        if (StringUtils.isEmpty(base64UrlEncodeString)) {
            return base64UrlEncodeString;
        }
        StringBuilder ret = new StringBuilder();
        for (Character c : base64UrlEncodeString.toCharArray()) {
            Character v = map.get(c);
            if (v == null) {
                throw new IllegalArgumentException("存在非法字符：" + c);
            }
            ret.append(v);
        }
        return ret.toString();
    }

}
