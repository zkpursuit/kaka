package com.kaka.util.text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词匹配过滤算法
 */
public class DFA {

    /**
     * 敏感字匹配类型
     */
    public enum MatchType {

        /**
         * 关键字最小匹配规则
         */
        /**
         * 关键字最小匹配规则
         */
        MIN,
        /**
         * 关键字最大匹配规则
         */
        MAX
    }

    private Map<Object, Object> sensitiveWordMap;
    private int keyMaxLen = 0;

    public DFA() {

    }

    public DFA(Set<String> sensitiveWordStore) {
        initKeywordStore(sensitiveWordStore);
    }

    protected final void initKeywordStore(Set<String> sensitiveWordStore) {
        this.sensitiveWordMap = addKeywordToHashMap(sensitiveWordStore);
    }

    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：
     *
     * 中 = { isEnd = 0 国 = {
     *
     * isEnd = 1 人 = {isEnd = 0 民 = {isEnd = 1} } 男 = { isEnd = 0 人 = { isEnd =
     * 1 } } } } 五 = { isEnd = 0 星 = { isEnd = 0 红 = { isEnd = 0 旗 = { isEnd = 1
     * } } } }
     *
     */
    private Map<Object, Object> addKeywordToHashMap(Set<String> wordSet) {
        // 初始化敏感词容器，减少扩容操作
        Map<Object, Object> wordMap = new HashMap<>(wordSet.size());
        wordSet.stream().forEach((word) -> {
            if (word.length() > keyMaxLen) {
                keyMaxLen = word.length();
            }
            Map<Object, Object> nowMap = wordMap;
            for (int i = 0; i < word.length(); i++) {
                // 转换成char型
                char keyChar = word.charAt(i);
                // 获取
                Object tempMap = nowMap.get(keyChar);
                // 如果存在该key，直接赋值
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                } else {
                    // 不存在构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    // 设置标志位
                    Map<Object, Object> newMap = new HashMap<>();
                    newMap.put("isEnd", "0");
                    // 添加到集合
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                // 最后一个
                if (i == word.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        });
        return wordMap;
    }

    /**
     * 判断文字是否包含敏感字符
     *
     * @param txt
     * @param matchType
     * @return
     */
    public boolean isContaintKeyword(String txt, MatchType matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            // 判断是否包含敏感字符
            int matchFlag = checkKeyword(txt, i, matchType);
            // 大于0存在，返回true
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：
     *
     * 如果存在，则返回敏感词字符的长度，不存在返回0
     *
     * @param text
     * @param beginIndex
     * @param matchType
     * @return
     */
    private int checkKeyword(String text, int beginIndex, MatchType matchType) {
        // 敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        // 匹配标识数默认为0
        int matchFlag = 0;
        Map<Object, Object> nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < text.length() && (i - beginIndex) < keyMaxLen; i++) {
            char word = text.charAt(i);
            // 获取指定key
            nowMap = (Map) nowMap.get(word);
            // 存在，则判断是否为最后一个
            if (nowMap != null) {
                // 找到相应key，匹配标识+1
                matchFlag++;
                // 如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    // 结束标志位为true
                    flag = true;
                    // 最小规则，直接返回,最大规则还需继续查找
                    if (MatchType.MIN == matchType) {
                        break;
                    }
                }
            } // 不存在，直接返回
            else {
                break;
            }
        }
        // 长度必须大于等于1，为词
        if (matchFlag < 1 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 获取文本中包含的所有关键词
     *
     * @param txt
     * @param matchType
     * @return
     */
    public Set<String> getAllKeywords(String txt, MatchType matchType) {
        Set<String> sensitiveWordList = new HashSet<>();
        for (int i = 0; i < txt.length(); i++) {
            // 判断是否包含敏感字符
            int length = checkKeyword(txt, i, matchType);
            // 存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                // 减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }
        return sensitiveWordList;
    }
    
    public void findKeywords(String text, MatchType matchType, final Hit<String> vistor) {
        for (int i = 0; i < text.length(); i++) {
            // 判断是否包含敏感字符
            int length = checkKeyword(text, i, matchType);
            // 存在,加入list中
            if (length > 0) {
                int begin = i, end = i + length;
                String value = text.substring(begin, end);
                if (vistor != null) {
                    vistor.visit(begin, end, value);
                }
                // 减1的原因，是因为for会自增
                //i = i + length - 1;
            }
        }
    }

//    /**
//     * 替换敏感字字符，DFA算法
//     *
//     * @param txt
//     * @param matchType
//     * @param replaceChar
//     * @return
//     */
//    public String replaceKeyword(String txt, MatchType matchType, String replaceChar) {
//        String resultTxt = txt;
//        // 获取所有的敏感词
//        Set<String> set = getKeywords(txt, matchType);
//        String replaceString;
//        for (String word : set) {
//            replaceString = getReplaceChars(replaceChar, word.length());
//            resultTxt = resultTxt.replaceAll(word, replaceString);
//        }
//        return resultTxt;
//    }
//    /**
//     * 获取替换字符串
//     *
//     * @param replaceChar
//     * @param length
//     * @return
//     */
//    private String getReplaceChars(String replaceChar, int length) {
//        String resultReplace = replaceChar;
//        for (int i = 1; i < length; i++) {
//            resultReplace += replaceChar;
//        }
//        return resultReplace;
//    }
}
