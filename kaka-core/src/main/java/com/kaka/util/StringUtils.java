package com.kaka.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串辅助工具集
 *
 * @author zkpursuit
 */
public class StringUtils {

    private static final char[] namechars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    /**
     * 随机生成字符串
     *
     * @param chars      字符数组
     * @param count      字符串字符个数
     * @param charRepeat 是否允许字符串中有重复字符，true为可以有重复字符
     * @return 随机的字符串
     */
    public final static String randomString(char[] chars, int count, boolean charRepeat) {
        StringBuilder sb = new StringBuilder();
        int len = chars.length - 1;
        int index;
        char c;
        int ci = 0;
        while (ci < count) {
            if (!charRepeat) {
                do {
                    c = chars[MathUtils.getRandom().nextInt(len)];
                    int length = sb.length();
                    index = -1;
                    for (int i = 0; i < length; i++) {
                        char _c = sb.charAt(i);
                        if (_c == c) {
                            index = i;
                            break;
                        }
                    }
                } while (index >= 0);
            } else {
                c = chars[MathUtils.getRandom().nextInt(len)];
            }
            sb.append(c);
            ci++;
        }
        return sb.toString();
    }

    /**
     * 随机生成字符串
     *
     * @param count      字符串字符个数
     * @param charRepeat 是否允许字符串中有重复字符，true为可以有重复字符
     * @return 包含（A-Z）或（a-z）或（0-9）的字符串
     */
    public final static String randomString(int count, boolean charRepeat) {
        return randomString(namechars, count, charRepeat);
    }

    /**
     * 字符串替换
     *
     * @param s    源字符串
     * @param sub  被替换的字符
     * @param with 替换的字符
     * @return 替换后的新字符串
     */
    public static String replace(String s, String sub, String with) {
        int c = 0;
        int i = s.indexOf(sub, c);
        if (i == -1) {
            return s;
        }
        StringBuilder buf = new StringBuilder(s.length() + with.length());
        do {
            buf.append(s.substring(c, i));
            buf.append(with);
            c = i + sub.length();
        } while ((i = s.indexOf(sub, c)) != -1);
        if (c < s.length()) {
            buf.append(s.substring(c, s.length()));
        }
        return buf.toString();
    }

    /**
     * 将src字符串中的regex字符串依次替换为args中的值
     *
     * @param src   源字符串
     * @param regex 需替换的正则表达式
     * @param args  被替换的字符串
     * @return 替换后的字符串
     */
    public final static String replace(String src, String regex, Object... args) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(src);
        StringBuffer sb = new StringBuffer();
        for (Object arg : args) {
            if (!matcher.find()) {
                break;
            }
            matcher.appendReplacement(sb, String.valueOf(arg));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将number数字转为为count位的字符串，不足count位，前面补0
     *
     * @param number 正整数
     * @param count  位数
     * @return count位字符串
     */
    public static String repairZeroToFirst(int number, int count) {
        return String.format("%0" + count + "d", number);
    }

    /**
     * 将number数字转为为count位的字符串，不足count位，后面补0
     *
     * @param number 正整数
     * @param count  位数
     * @return count位字符串
     */
    public static String repairZeroToLast(int number, int count) {
        return String.format("%1$0" + count + "d", number);
    }

    // private static void getNextval(char[] p, int[] next) {
    //     int pLen = p.length;
    //     next[0] = -1;
    //     int k = -1;
    //     int j = 0;
    //     while (j < pLen - 1) {
    //         //p[k]表示前缀，p[j]表示后缀
    //         if (k == -1 || p[j] == p[k]) {
    //             ++j;
    //             ++k;
    //             //较之前next数组求法，改动在下面4行
    //             if (p[j] != p[k])
    //                 next[j] = k;   //之前只有这一行
    //             else
    //                 //因为不能出现p[j] = p[ next[j ]]，所以当出现时需要继续递归，k = next[k] = next[next[k]]
    //                 next[j] = next[k];
    //         } else {
    //             k = next[k];
    //         }
    //     }
    // }

    // /**
    //  * kmp字符串查找算法 </br>
    //  * 参考博文：https://www.cnblogs.com/ZuoAndFutureGirl/p/9028287.html </br>
    //  * 经jdk8上测试简单英文字符串匹配搜索100000+次，性能不及String原生indexOf算法
    //  *
    //  * @param src  源字符串
    //  * @param pstr 搜索的字符串
    //  * @return
    //  */
    // public static int indexOfByKmp(String src, String pstr) {
    //     int i = 0;
    //     int j = 0;
    //     int sLen = src.length();
    //     int pLen = pstr.length();
    //     char[] s = src.toCharArray();
    //     char[] p = pstr.toCharArray();
    //     int[] next = new int[pLen];
    //     getNextval(p, next);
    //     while (i < sLen && j < pLen) {
    //         //①如果j = -1，或者当前字符匹配成功（即S[i] == P[j]），都令i++，j++
    //         if (j == -1 || s[i] == p[j]) {
    //             i++;
    //             j++;
    //         } else {
    //             //②如果j != -1，且当前字符匹配失败（即S[i] != P[j]），则令 i 不变，j = next[j]
    //             //next[j]即为j所对应的next值
    //             j = next[j];
    //         }
    //     }
    //     if (j == pLen)
    //         return i - j;
    //     else
    //         return -1;
    // }

    /**
     * 获取src中匹配regex正则规则的字符串集合
     *
     * @param src             源字符串
     * @param regex           正则表达式
     * @param regexMatchIndex 0表示匹配整个表达式，其它正数表示正则表达式中的括号中所标示的子表达式
     * @return 字符串集合
     */
    public final static List<String> match(String src, String regex, int regexMatchIndex) {
        if (src == null) {
            return null;
        }
        if (regex == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(src);
        List<String> result = null;
        while (matcher.find()) {
            if (result == null) {
                result = new ArrayList<>();
            }
            result.add(matcher.group(regexMatchIndex));
        }
        return result;
    }

    /**
     * 获取src中匹配regex正则规则的字符串集合
     *
     * @param src   源字符串
     * @param regex 正则表达式
     * @return 字符串集合
     */
    public final static List<String> match(String src, String regex) {
        return match(src, regex, 0);
    }

    /**
     * 将src字符串中的{}字符串依次替换为args中的值
     *
     * @param src  源字符串
     * @param args 替换的新值列表
     * @return 新的字符串
     */
//    public final static String replace(String src, Object[] args) {
//        return replace(src, "\\{[ 0-9]*\\}", args);
//    }
    public final static String replace(String src, Object... args) {
        return replace(src, "\\{[ 0-9]*\\}", args);
    }

    /**
     * 获取src中匹配{数字}正则规则的字符串集合
     *
     * @param src 源字符串
     * @return 新的字符串
     */
    public final static List<String> match(String src) {
        return match(src, "\\{[0-9&]*\\}");
    }

    /**
     * 是否为数字的正则
     */
    private final static Pattern intPattern = Pattern.compile("^(\\-|\\+)?[0-9]*$");

    /**
     * 判断字符串是否为纯整型数字
     *
     * @param src 待判断的字符串
     * @return true为纯整型数字
     */
    public final static boolean isInteger(String src) {
        if (src == null) {
            return false;
        }
        if ("".equals(src)) {
            return false;
        }
        return intPattern.matcher(src).matches();
    }

    /**
     * 是否为数字的正则
     */
    private final static Pattern numberPattern = Pattern.compile("^(\\-|\\+)?[0-9]*(\\.[0-9]*)?$");

    /**
     * 判断字符串是否为数字
     *
     * @param src 待判断的字符串
     * @return true为数字
     */
    public final static boolean isNumeric(String src) {
        if (src == null) {
            return false;
        }
        if ("".equals(src)) {
            return false;
        }
        return numberPattern.matcher(src).matches();
    }

    /**
     * 判断字符串是否不为null或者""
     *
     * @param src 源字符串
     * @return true不为null或者""，false为null或者""
     */
    public final static boolean isNotEmpty(String src) {
        if (src == null) {
            return false;
        }
        return !"".equals(src);
    }

}
