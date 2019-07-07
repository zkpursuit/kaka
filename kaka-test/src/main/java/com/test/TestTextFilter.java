package com.test;

import com.kaka.util.text.DFA;
import com.kaka.util.text.DoubleArrayTrie;
import com.kaka.util.ResourceUtils;
import com.kaka.util.TextFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TestTextFilter {

    public static void main(String... args) throws IOException {
        String text = "草把学生整你妹的我日陈水扁我日被中共KO了找妹妹操逼我日的汗啊 陈水扁啊啊啊啊的办理本科方式发到双方的首发第三方的手佛挡杀佛第三方第三方的手陈水扁A";
//        InputStream is = Tool.getResourceAsStream(TestSensitiveWordFilter.class, "text.txt");
//        text = StringUtils.inputStreamToString(is, Charset.utf8);
//        is.close();
//        text = "你好我你好我好他好你妈B的陈水扁，KO";
//        text = "找妹妹操逼洞我日的汗啊";
//        System.out.println(text);

        int count = 1000;
        testDFA("sensitivewords_2.txt", text, count, true);
        testDAT("sensitivewords_2.txt", text, count, true);
    }

    private static void testDFA(String sensitivewordsFileName, String text, int execCount, boolean printResult) throws IOException {
        Set<String> keywords = new HashSet<>();
        try (InputStream is = ResourceUtils.getResourceAsStream(sensitivewordsFileName, TestTextFilter.class)) {
            try (BufferedReader dr = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = dr.readLine()) != null) {
                    String str = line.trim();
                    if (!"".equals(str)) {
                        keywords.add(str);
                    }
                }
            }
        }
        DFA dfa = new DFA(keywords);
        //以上为一次构建到处使用，即实际使用时，DFA为单例
        System.out.println("DFA ----------------------------------------------");
        long time1 = System.currentTimeMillis();
//        List<DFA.Hit> hitList = dfa.findAllKeywords(text, DFA.MatchType.MIN);
//        hitList.forEach((hit) -> {
//            System.out.print("[" + hit.begin + ", " + hit.end + "], " + hit.value + "\t");
//        });
//        System.out.println("\n搜索到" + hitList.size() + "个关键词");

        String result = null;
        for (int i = 0; i < execCount; i++) {
            result = TextFilter.replace(text, '*', dfa, DFA.MatchType.MAX);
        }
        long time2 = System.currentTimeMillis();
        if (printResult) {
            System.out.println(result);
        }
        System.out.println("DFA执行" + execCount + "次耗时：==>> " + (time2 - time1));
    }

    public static void testDAT(String sensitivewordsFileName, String text, int execCount, boolean printResult) throws IOException {
        TreeSet<String> set = new TreeSet<>();
        try (InputStream is = ResourceUtils.getResourceAsStream(sensitivewordsFileName, TestTextFilter.class)) {
            try (BufferedReader dr = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = dr.readLine()) != null) {
                    String str = line.trim();
                    if (!"".equals(str)) {
                        set.add(str);
                    }
                }
            }
        }
        List<String> list = new ArrayList<>();
        set.forEach((String str) -> {
            list.add(str);
        });
        DoubleArrayTrie dat = new DoubleArrayTrie();
        dat.build(list);
        //以上为一次构建到处使用，即实际使用时，DoubleArrayTrie为单例

        System.out.println("DAT ----------------------------------------------");
        long time1 = System.currentTimeMillis();
//        List<DoubleArrayTrie.Hit> hitList = dat.findAllKeywords(text);
//        hitList.forEach((hit) -> {
//            System.out.print("[" + hit.begin + ", " + hit.end + "], " + list.get(hit.keyIndex) + "\t");
//        });
//        System.out.println("\n搜索到" + hitList.size() + "个关键词");

        String result = null;
        for (int i = 0; i < execCount; i++) {
            result = TextFilter.replace(text, '*', dat);
        }
        long time2 = System.currentTimeMillis();
        if (printResult) {
            System.out.println(result);
        }
        System.out.println("DAT执行" + execCount + "次耗时：==>> " + (time2 - time1));
    }

}
