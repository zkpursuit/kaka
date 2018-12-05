package com.kaka.util;

/**
 * 字符编码常量
 *
 * @author zhoukai
 */
public class Charsets {

    /**
     * UTF-8字符集
     */
    public static final java.nio.charset.Charset utf8 = java.nio.charset.Charset.forName("UTF-8");

    /**
     * GBK字符集
     */
    public static final java.nio.charset.Charset gbk = java.nio.charset.Charset.forName("GBK");

    /**
     * GB2312字符集
     */
    public static final java.nio.charset.Charset gb2312 = java.nio.charset.Charset.forName("GB2312");
    
    /**
     * ISO-8859-1字符集
     */
    public static final java.nio.charset.Charset iso_8859_1 = java.nio.charset.Charset.forName("ISO-8859-1");
    
    /**
     * 系统默认字符集
     */
    public static final java.nio.charset.Charset sys = java.nio.charset.Charset.defaultCharset();

    /**
     * 私有构造，不允许外部实例化
     */
    private Charsets() {
    }

}
