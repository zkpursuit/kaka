package com.kaka.util;

import com.kaka.util.text.DFA;
import com.kaka.util.text.DoubleArrayTrie;

import java.util.Set;
import java.util.TreeSet;

public class TextFilter {

    /**
     * DFA算法替换敏感词
     *
     * @param text        需替换的原文本
     * @param replaceChar 每个敏感词中的字被替换的字符
     * @param dfa         全局唯一，引用前必须在构造方法里传入敏感词库（Set)
     * @param matchType   匹配类型
     * @return 替换敏感词后的文本
     */
    public static final String replace(String text, final char replaceChar, DFA dfa, DFA.MatchType matchType) {
        final char[] chars = text.toCharArray();
        dfa.findKeywords(text, matchType, (int begin, int end, String value) -> {
            for (int i = begin; i < end; i++) {
                chars[i] = replaceChar;
            }
        });
        return new String(chars);
    }

    /**
     * 双数组字典算法替换敏感词<br>
     * 此法要优于DFA算法，词典比较大，处理的文本比较小时的性能要优于ACDAT算法<br>
     * 一般情况（比如聊天中敏感词替换）应尽可能的使用此方法
     *
     * @param text        需替换的原文本
     * @param replaceChar 每个敏感词中的字被替换的字符
     * @param dat         全局唯一，引用前必须调用build方法初始化敏感词库，build方法参数必须为字典序，故而此参数可先用TreeSet再用List构建
     * @return 替换敏感词后的文本
     */
    public static final String replace(String text, final char replaceChar, DoubleArrayTrie dat) {
        final char[] chars = text.toCharArray();
        dat.findKeywords(text, (int begin, int end, Integer keyIndex) -> {
            for (int i = begin; i < end; i++) {
                chars[i] = replaceChar;
            }
        });
        return new String(chars);
    }

    /**
     * DFA算法替换敏感词
     *
     * @param text        需替换的原文本
     * @param replaceText 每个敏感词的每个字符替换的文本
     * @param dfa         全局唯一，引用前必须在构造方法里传入敏感词库（Set)
     * @param matchType   匹配类型
     * @return 替换敏感词后的文本
     */
    public static final String replace(String text, String replaceText, DFA dfa, DFA.MatchType matchType) {
        if (replaceText.length() == 1) {
            char replaceChar = replaceText.charAt(0);
            return replace(text, replaceChar, dfa, matchType);
        }
        Set<Integer> replaceIdxs = new TreeSet<>();
        dfa.findKeywords(text, matchType, (int begin, int end, String value) -> {
            for (int i = begin; i < end; i++) {
                replaceIdxs.add(i);
            }
        });
        StringBuilder sb = new StringBuilder();
        int[] start = new int[1];
        replaceIdxs.forEach((idx) -> {
            if (idx > 0 && (idx - start[0]) > 0) {
                sb.append(text.substring(start[0], idx));
            }
            sb.append(replaceText);
            start[0] = idx + 1;
        });
        if (start[0] > 0 && start[0] < text.length()) {
            sb.append(text.substring(start[0], text.length()));
        }
        return sb.toString();
    }

    /**
     * 双数组字典算法替换敏感词<br>
     * 此法要优于DFA算法，词典比较大，处理的文本比较小时的性能要优于ACDAT算法<br>
     * 一般情况（比如聊天中敏感词替换）应尽可能的使用此方法
     *
     * @param text        需替换的原文本
     * @param replaceText 每个敏感词的每个字符替换的文本
     * @param dat         全局唯一，引用前必须调用build方法初始化敏感词库，build方法参数必须为字典序，故而此参数可先用TreeSet再用List构建
     * @return 替换敏感词后的文本
     */
    public static final String replace(String text, String replaceText, DoubleArrayTrie dat) {
        if (replaceText.length() == 1) {
            char replaceChar = replaceText.charAt(0);
            return replace(text, replaceChar, dat);
        }
        Set<Integer> replaceIdxs = new TreeSet<>();
        dat.findKeywords(text, (int begin, int end, Integer keyIndex) -> {
            for (int i = begin; i < end; i++) {
                replaceIdxs.add(i);
            }
        });
        StringBuilder sb = new StringBuilder();
        int[] start = new int[1];
        for (Integer idx : replaceIdxs) {
            if (idx > 0 && (idx - start[0]) > 0) {
                sb.append(text.substring(start[0], idx));
            }
            sb.append(replaceText);
            start[0] = idx + 1;
        }
        if (start[0] > 0 && start[0] < text.length()) {
            sb.append(text.substring(start[0], text.length()));
        }
        return sb.toString();
    }

    private TextFilter() {

    }

}
