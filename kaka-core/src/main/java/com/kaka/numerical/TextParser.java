package com.kaka.numerical;

/**
 * excel文件复制成的制表符分割的txt文件数据行解析器
 *
 * @author zhoukai
 */
public class TextParser extends Parser {

    private TextAnalyzer analyzer;

    /**
     * 将每行数据序列化为对象
     *
     * @param <T> 序列化的目标对象类型
     * @param lineDatas 一行数据
     * @param titles 列名集合
     * @param infoClass 序列化的目标对象Class
     * @return 序列化后的目标对象
     */
    public <T> T doParse(String[] lineDatas, String[] titles, Class<T> infoClass) {
        if (analyzer == null) {
            analyzer = new TextAnalyzer();
        }
        analyzer.setSourceData(new String[][]{lineDatas, titles});
        return this.doParse(infoClass, analyzer);
    }

}
