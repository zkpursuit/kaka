package com.kaka.numerical;

import java.util.HashMap;
import java.util.Map;

/**
 * excel文件复制成的制表符分割的txt文件分析器，用于分析每行每个单元格内容
 *
 * @author zhoukai
 */
public class TextAnalyzer implements IAnalyzer<String[][]> {

    private final Map<String, String> title_content_map = new HashMap<>();

    /**
     * 设置数据行，每行包括数个单元格内容
     *
     * @param sourceData 一行数据,列名集合
     */
    @Override
    public void setSourceData(String[][] sourceData) {
        String[] lineDatas = sourceData[0]; //一行数据
        String[] titles = sourceData[1]; //列名集合
        title_content_map.clear();
        for (int i = 0; i < lineDatas.length; i++) {
            String title = titles[i].trim();
            String value = lineDatas[i].trim();
            if ("".equals(title)) {
                throw new Error("存在空列名（列号>>" + (i + 1) + "）");
            }
            if (title_content_map.containsKey(title)) {
                throw new Error("存在重复的列名（列号>>" + (i + 1) + "）：" + title);
            }
            title_content_map.put(title, value);
        }
    }

    /**
     * 通过列名在数据行中查找单元格内容
     *
     * @param title 单元格所在列名
     * @return 单元格内容，未匹配到列名返回null
     */
    @Override
    public String getContent(String title) {
        if (title_content_map.isEmpty()) {
            return null;
        }
        if (title == null) {
            return null;
        }
        return title_content_map.get(title);
    }
}
