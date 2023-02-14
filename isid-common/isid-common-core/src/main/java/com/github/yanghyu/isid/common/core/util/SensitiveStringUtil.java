package com.github.yanghyu.isid.common.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SensitiveStringUtil {

    /**
     * 敏感数字字符串识别长度
     */
    private static final int SENSITIVE_NUMERIC_CHARACTERS_DISTINGUISH_LENGTH = 6;

    /**
     * 非敏感数据前缀数组
     */
    private static final ArrayList<String> NON_SENSITIVE_PREFIX = new ArrayList<>();

    /**
     * 默认敏感的汉字
     */
    private static final HashSet<Character> SENSITIVE_HAN_ZI = new HashSet<>();

    /**
     * 敏感的汉字
     */
    private static HashSet<Character> sensitiveHanZi;

    static {
        NON_SENSITIVE_PREFIX.add("OBEN");
        NON_SENSITIVE_PREFIX.add("OBEU");
        NON_SENSITIVE_PREFIX.add("OBYZT");
        NON_SENSITIVE_PREFIX.add("SCFP");
        NON_SENSITIVE_PREFIX.add("OBEA");

        Character[] senHanZiA = new Character[]{'京', '湘', '津', '沪', '粤', '渝', '琼', '冀', '川', '晋', '贵', '黔',
                '辽', '云', '滇', '吉', '陕', '豫', '甘', '苏', '青', '浙', '台', '皖', '藏', '闽', '蒙', '赣', '桂', '鲁',
                '宁', '黑', '新', '港', '澳', '鄂', '申', '华', '使', '领', '挂', '学', '警'};
        SENSITIVE_HAN_ZI.addAll(Arrays.asList(senHanZiA));

        Character[] senHanZiB = new Character[]{'国', '省', '市', '县', '区', '乡', '镇', '村', '州', '路', '自', '治',
                '广', '湖', '南', '北', '东', '西', '中', '江', '河', '湖', '海', '重', '山', '安', '徽', '福', '夏', '疆',
                '圳', '夏', '大', '街', '道', '号', '里', '旗' , '栋', '盟'};
        SENSITIVE_HAN_ZI.addAll(Arrays.asList(senHanZiB));

        String baiJiaXin = "赵钱孙李，周吴郑王" + "冯陈褚卫，蒋沈韩杨" + "朱秦尤许，何吕施张" + "孔曹严华，金魏陶姜" +
                "戚谢邹喻，柏水窦章" + "云苏潘葛，奚范彭郎" + "鲁韦昌马，苗凤花方" + "俞任袁柳，酆鲍史唐" + "费廉岑薛，雷贺倪汤" +
                "滕殷罗毕，郝邬安常" + "乐于时傅，皮卞齐康" + "伍余元卜，顾孟平黄" + "和穆萧尹，姚邵湛汪" + "祁毛禹狄，米贝明臧" +
                "计伏成戴，谈宋茅庞" + "熊纪舒屈，项祝董梁" + "杜阮蓝闵，席季麻强" + "贾路娄危，江童颜郭" + "梅盛林刁，钟徐邱骆" +
                "高夏蔡田，樊胡凌霍" + "虞万支柯，昝管卢莫" + "经房裘缪，干解应宗" + "丁宣贲邓，郁单杭洪" + "包诸左石，崔吉钮龚" +
                "程嵇邢滑，裴陆荣翁" + "荀羊於惠，甄曲家封" + "芮羿储靳，汲邴糜松" + "井段富巫，乌焦巴弓" + "牧隗山谷，车侯宓蓬" +
                "全郗班仰，秋仲伊宫" + "宁仇栾暴，甘钭厉戎" + "祖武符刘，景詹束龙" + "叶幸司韶，郜黎蓟薄" + "印宿白怀，蒲邰从鄂" +
                "索咸籍赖，卓蔺屠蒙" + "池乔阴鬱，胥能苍双" + "闻莘党翟，谭贡劳逄" + "姬申扶堵，冉宰郦雍" + "郤璩桑桂，濮牛寿通" +
                "边扈燕冀，郏浦尚农" + "温别庄晏，柴瞿阎充" + "慕连茹习，宦艾鱼容" + "向古易慎，戈廖庾终" + "暨居衡步，都耿满弘" +
                "匡国文寇，广禄阙东" + "欧殳沃利，蔚越夔隆" + "师巩厍聂，晁勾敖融" + "冷訾辛阚，那简饶空" + "曾毋沙乜，养鞠须丰" +
                "巢关蒯相，查后荆红" + "游竺权逯，盖益桓公";
        for (Character c: baiJiaXin.toCharArray()) {
            SENSITIVE_HAN_ZI.add(c);
        }
        SENSITIVE_HAN_ZI.remove('，');
        sensitiveHanZi = SENSITIVE_HAN_ZI;
    }

    public static void setSensitiveHanZiList(Set<Character> characterList) {
        if (characterList == null) {
            sensitiveHanZi = SENSITIVE_HAN_ZI;
        } else {
            sensitiveHanZi = new HashSet<>(characterList);
        }
    }

    /**
     * 敏感信息脱敏
     *
     * @param source 待脱敏字符串
     * @return 脱敏后字符串
     */
    public static String desensitize(String source) {
        StringBuilder s = numericAndHanCharactersDesensitize(source);
        s = macAndIpv4AndMailDesensitize(s);
        secretKeyDesensitize(s, "ecret");
        return s.toString();
    }
    
//    /**
//     * 敏感数字字符串脱敏
//     * <p>
//     * 发现连续数字字符长度超过六个时，需要对这串字符做识别,若是敏感信息则掩码，若不是敏感信息则每六个字符后增加一个**隔离。
//     *
//     * @param source 待脱敏字符串
//     * @return 脱敏后字符串
//     */
//    public static StringBuilder numericCharactersDesensitize(CharSequence source) {
//        StringBuilder retBuilder = new StringBuilder();
//        if (source != null && source.length() > 0) {
//            StringBuilder numBuilder = new StringBuilder();
//            int totalLength = source.length();
//            for (int i = 0; i < totalLength; i++) {
//                char c = source.charAt(i);
//                if (CharUtil.isValidNumber(c)) {
//                    numBuilder.append(c);
//                } else {
//                    if (numBuilder.length() > 0) {
//                        retBuilder.append(numericCharactersConvert(retBuilder, numBuilder));
//                        numBuilder = new StringBuilder();
//                    }
//                    retBuilder.append(c);
//                }
//            }
//            if (numBuilder.length() > 0) {
//                retBuilder.append(numericCharactersConvert(retBuilder, numBuilder));
//            }
//        }
//        return retBuilder;
//    }

    /**
     * 敏感数字字符串和汉字字符串脱敏
     * <p>
     * 发现连续数字字符长度超过六个时，需要对这串字符做识别,若是敏感信息则掩码，若不是敏感信息则每六个字符后增加一个**隔离。
     * 发现汉字时也要做脱敏处理。
     *
     * @param source 待脱敏字符串
     * @return 脱敏后字符串
     */
    public static StringBuilder numericAndHanCharactersDesensitize(CharSequence source) {
        StringBuilder retBuilder = new StringBuilder();
        if (source != null && source.length() > 0) {
            int lastCharType = 0; // 0:无 1:数字 2:汉字 3:其它
            StringBuilder fragmentBuilder = new StringBuilder();
            int totalLength = source.length();
            for (int i = 0; i < totalLength; i++) {
                char c = source.charAt(i);
                int currentCharType = CharUtil.charType(c);
                if (lastCharType == currentCharType) {
                    fragmentBuilder.append(c);
                } else {
                    if (lastCharType == 1) {
                        retBuilder.append(numericCharactersConvert(retBuilder, fragmentBuilder));
                    } else if (lastCharType == 2) {
                        retBuilder.append(hanCharactersConvert(fragmentBuilder));
                    } else if (fragmentBuilder.length() > 0) {
                        retBuilder.append(fragmentBuilder);
                    }

                    fragmentBuilder = new StringBuilder();
                    fragmentBuilder.append(c);
                    lastCharType = currentCharType;
                }
            }
            if (lastCharType == 1) {
                retBuilder.append(numericCharactersConvert(retBuilder, fragmentBuilder));
            } else if (lastCharType == 2) {
                retBuilder.append(hanCharactersConvert(fragmentBuilder));
            } else if (fragmentBuilder.length() > 0) {
                retBuilder.append(fragmentBuilder);
            }
        }
        return retBuilder;
    }

    private static StringBuilder numericCharactersConvert(StringBuilder retBuilder, StringBuilder fragmentBuilder) {
        int numLen = fragmentBuilder.length();
        if (numLen < SENSITIVE_NUMERIC_CHARACTERS_DISTINGUISH_LENGTH) {
            // 0~5 不是敏感数据
            return fragmentBuilder;
        } else if (numLen < 10) {
            // 6~9 基本不是敏感信息（需要排除是座机号可能），加入混淆，避免误中
            int retLen = retBuilder.length();
            String prefix = retBuilder.substring(retLen > 10 ? retLen - 10 : 0);
            if (prefix != null && prefix.endsWith("-")) {
                fragmentBuilder.replace(0, 6, "******");
            } else {
                fragmentBuilder.insert(5, "**");
            }
        } else if (numLen < 15) {
            // 10~14 基本就是敏感信息。
            fragmentBuilder.replace(2, 10, "********");
        } else if (numLen < 18) {
            // 15~17 可能是用户ID，企业ID等，其它情况基本就是敏感信息。
            int retLen = retBuilder.length();
            String prefix = retBuilder.substring(retLen > 10 ? retLen - 10 : 0);
            if (prefix != null && prefix.length() > 0) {
                for (String pre : NON_SENSITIVE_PREFIX) {
                    if (prefix.endsWith(pre)) {
                        return fragmentBuilder;
                    }
                }
            }
            fragmentBuilder.replace(2, 14, "************");
        } else {
            // 18～+ 基本可确定是身份证，按身份证方式掩码。
            fragmentBuilder.replace(2, 16, "**************");
        }
        return fragmentBuilder;
    }

    private static StringBuilder hanCharactersConvert(StringBuilder fragmentBuilder) {
        // 根据长度初步屏蔽
        int len = fragmentBuilder.length();
        switch (len) {
            case 0:
                break;
            case 1: //省会简称
            case 2: //二字姓名等词汇
                fragmentBuilder.replace(0, 1, "*");
                break;
            case 3: //三字姓名等词汇
                fragmentBuilder.replace(0, 2, "**");
                break;
            case 4: //四字姓名等词汇
                fragmentBuilder.replace(0, 1, "*");
                fragmentBuilder.replace(2, 3, "*");
                break;
            default:
        }
        // 根据敏感词屏蔽并添加竖线表示一个中文
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = fragmentBuilder.charAt(i);
            if (c == '*' || sensitiveHanZi.contains(c)) {
                builder.append("*");
            } else {
                builder.append(c);
                builder.append('|');
            }
        }
        return builder;
    }

    /**
     * MAC、IPV4、mail等字符串脱敏
     * <p>
     * 发现敏感字符串后按规则屏蔽，宁紧勿松
     * <p>
     * 例
     * 11:22:33:44:55:69
     * 11-22-33-44-55-69
     * <p>
     * 192.168.1.2
     * <p>
     * yanghongyu@pingan.com.cn
     *
     * @param source 待脱敏字符串
     * @return 脱敏后字符串
     */
    public static StringBuilder macAndIpv4AndMailDesensitize(CharSequence source) {
        StringBuilder retBuilder = new StringBuilder();
        if (source != null && source.length() > 0) {
            StringBuilder senBuilder = new StringBuilder();
            int totalLength = source.length();
            char tagChar = ' ';
            int tagCharNum = 0;
            int tagCharTailLen = 0;
            for (int i = 0; i < totalLength; i++) {
                char c = source.charAt(i);
                if (tagChar == ' ') {
                    if (CharUtil.isSpacerChar(c)) {
                        tagChar = c;
                        senBuilder.append(c);
                        tagCharNum = 1;
                        tagCharTailLen = 1;
                    } else {
                        retBuilder.append(c);
                    }
                } else if (c == tagChar) {
                    senBuilder.append(c);
                    tagCharNum++;
                    tagCharTailLen = 1;
                } else if (CharUtil.isSpacerChar(c)) {
                    retBuilder.append(macAndIpv4DesensitizeHandle(senBuilder, tagCharNum, tagChar));
                    senBuilder = new StringBuilder();
                    tagChar = c;
                    senBuilder.append(c);
                    tagCharNum = 1;
                    tagCharTailLen = 1;
                } else if (CharUtil.isValidNumber(c) && tagCharTailLen <= 3) {
                    senBuilder.append(c);
                    tagCharTailLen++;
                } else if (CharUtil.isValidHex(c) && tagCharTailLen <= 2) {
                    senBuilder.append(c);
                    tagCharTailLen++;
                } else {
                    // 将原有敏感缓冲池中数据进行脱敏处理，并添加到retBuilder中
                    retBuilder.append(macAndIpv4DesensitizeHandle(senBuilder, tagCharNum, tagChar));
                    senBuilder = new StringBuilder();
                    tagChar = ' ';
                    tagCharNum = 0;
                    tagCharTailLen = 0;
                    retBuilder.append(c);
                }
            }
            if (senBuilder.length() > 0) {
                // 将原有敏感缓冲池中数据进行脱敏处理，并添加到retBuilder中
                retBuilder.append(macAndIpv4DesensitizeHandle(senBuilder, tagCharNum, tagChar));
            }
            // 邮箱脱敏
            mailDesensitizeHandle(retBuilder);
        }
        return retBuilder;
    }

    private static StringBuilder macAndIpv4DesensitizeHandle(StringBuilder senBuilder, int tagCharNum, char tagChar) {
        int len = senBuilder.length();
        if (tagCharNum >= 3 && len >= 2 * tagCharNum) {
            int start = senBuilder.indexOf(String.valueOf(tagChar), 1);
            if (start < 0) {
                start = len / 2 - 1;
            } else {
                start += 1;
            }
            senBuilder.replace(start, len, CharUtil.nStarString(len - start));
        }
        return senBuilder;
    }

    private static void mailDesensitizeHandle(StringBuilder target) {
        int len = target.length();
        for (int i = 0; i < len; i++) {
            char c = target.charAt(i);
            if (c == '@') {
                int leftLimit = Math.max(i - 20, 0);
                int j = i - 1;
                for (; j >= leftLimit; j--) {
                    char jc = target.charAt(j);
                    if (!CharUtil.isValidMailChar(jc)) {
                        j++;
                        break;
                    }
                }
                if (i != j) {
                    if (i - 5 <= j) {
                        target.replace(j, i, CharUtil.nStarString(i - j));
                    } else {
                        target.replace(j + 3, i, CharUtil.nStarString(i - j - 3));
                    }
                }
            }
        }
    }

    /**
     * 密钥密码等字符串脱敏
     *
     * @param target 脱敏字符串对象
     * @param secretStr 脱敏敏感字
     */
    public static void secretKeyDesensitize(StringBuilder target, String secretStr) {
        if (target != null && target.length() > 0) {
            int length = target.length();
            int secLen = secretStr.length();
            int index = 0;
            do {
                index = target.indexOf(secretStr, index);
                if (index < 0) {
                    break;
                }
                index += secLen;
                int len = 0;
                for (; index < length; index++) {
                    char c = target.charAt(index);
                    if (CharUtil.isEndChar(c)) {
                        break;
                    } else if (CharUtil.isValidHex(c)) {
                        len++;
                        if (len > 64) {
                            break;
                        } else if (len > 3) {
                            target.replace(index, index + 1, "*");
                        }
                    }
                }

            } while (index > 0);
        }
    }

}
