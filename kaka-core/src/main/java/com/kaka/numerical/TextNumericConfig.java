package com.kaka.numerical;

import com.kaka.notice.Facade;
import com.kaka.util.StringUtils;

import java.io.*;

/**
 * 对复制excel表格数据内容生成的txt数值配置文件进行解析并转换为JavaBean对象<br>
 *
 * @param <T> 解析后的序列化对象类型（JavaBean对象类型）
 * @author zhoukai
 */
abstract public class TextNumericConfig<T> extends NumericConfig<T> {

    /**
     * 字段单元分隔符
     */
    private String delimiter = null;

    /**
     * 初始化文本行字段单元分隔符
     *
     * @return 字段单元分隔符
     */
    abstract protected String initDelimiter();
    
    /**
     * 获取字段单元分隔符
     * 
     * @return 字段单元分隔符
     */
    private String getDelimiter() {
        if(!StringUtils.isNotEmpty(delimiter)) {
            String str = "分隔符不能为空，请在子类中实现initDelimiter方法，并返回非空字符串；要使initDelimiter方法返回的分隔符生效，请使用";
            str += Facade.class.getTypeName();
            str += "对象注册本对象。";
            throw new Error(str);
        }
        return delimiter;
    }

    /**
     * 重写，使initDelimiter方法返回的分隔符生效
     */
    @Override
    protected void onRegister() {
        String _delimiter = initDelimiter();
        this.delimiter = _delimiter;
    }

    /**
     * 解析Excel的纯文本文件，默认从第1（序列号从0开始）行开始
     *
     * @param filePath 待解析的文件路径
     * @throws IOException           文件流处理异常
     * @throws FileNotFoundException 文件未找到异常
     */
    @Override
    public void parse(String filePath) throws IOException, FileNotFoundException {
        File file = new File(filePath);
        parse(file, "UTF-8", 1);
    }

    /**
     * 解析Excel复制成的纯文本内容
     *
     * @param file      待解析的文件
     * @param charset   读取文件所用编码
     * @param startLine 解析的开始行索引（序列号从0开始）
     * @throws UnsupportedEncodingException 不受支持的字符编码异常
     * @throws FileNotFoundException        文件未找到异常
     */
    public final void parse(File file, String charset, int startLine) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            parse(fis, charset, startLine);
        }
    }

    /**
     * 解析Excel复制成的纯文本内容
     *
     * @param is        文本内容流
     * @param charset   读取文件流的字符编码
     * @param startLine 解析的开始行索引（序列号从0开始）
     * @throws UnsupportedEncodingException 不受支持的字符编码异常
     */
    public final void parse(InputStream is, String charset, int startLine) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            parseBefore();
            String[] titles = null;
            String line;
            int sl = 0;
            TextParser parser = new TextParser();
            Class<T> beanClass = this.getMappingClass();
            while ((line = reader.readLine()) != null && !"".equals(line)) {
                if ("".equals(line)) {
                    continue;
                }
                if (sl < startLine) {
                    sl++;
                    continue;
                }
                String[] str1s = line.split(getDelimiter());
                if (titles == null) {
                    titles = str1s;
                } else {
                    T info = parser.doParse(str1s, titles, beanClass);
                    cacheObject(info);
                }
            }
            parseAfter();
        }
    }

    /**
     * 解析文本内容，以\\n结束符分行，\\t分隔行元素
     *
     * @param content   文本内容
     * @param startLine 解析的开始行索引（序列号从0开始）
     */
    public final void parse(String content, int startLine) {
        parseBefore();
        String[] lines = content.split("\n");
        String[] keys = null;
        int sl = 0;
        TextParser parser = new TextParser();
        Class<T> beanClass = this.getMappingClass();
        for (String line : lines) {
            if ("".equals(line)) {
                continue;
            }
            if (sl < startLine) {
                sl++;
                continue;
            }
            String[] str1s = line.split(getDelimiter());
            if (keys == null) {
                keys = str1s;
            } else {
                T info = parser.doParse(str1s, keys, beanClass);
                cacheObject(info);
            }
        }
        parseAfter();
    }

}
