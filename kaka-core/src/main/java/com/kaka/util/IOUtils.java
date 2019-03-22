package com.kaka.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * IO流相关工具集
 *
 * @author zkpursuit
 */
public class IOUtils {

    public static final int bufferSize = 64 * 1024;

    /**
     * 从输入流中读取字节数据
     *
     * @param in 输入流
     * @param dest 目标字节数组，存储输入流中的字节数据
     * @param destPos 存入目标字节数组时的起始位置
     * @param length 从输入流中读取字节的数量
     * @return 字节数组
     * @throws IOException
     */
    private static int readBytes(InputStream in, byte[] dest, int destPos, int length) throws IOException {
        if (in == null) {
            return -1;
        }
        if (length == 0) {
            return -1;
        }
        int c = in.read();
        if (c == -1) {
            return -1;
        }
        dest[destPos] = (byte) c;
        int i = 1;
        try {
            for (; i < length; i++) {
                c = in.read();
                if (c == -1) {
                    break;
                }
                dest[destPos + i] = (byte) c;
            }
        } catch (IOException ee) {
        }
        return i;
    }

    /**
     * 从输入流中读取字节数据
     *
     * @param in 输入流
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] readBytes(final InputStream in) throws IOException {
        return readBytes(in, bufferSize);
    }

    /**
     * 从输入流中读取字节数据
     *
     * @param input 输入流
     * @param size 预估大小
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] readBytes(final InputStream input, final int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }
        if (size == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[size];
        int count = readBytes(input, bytes, 0, size);
        if (size > count) {
            byte[] newBytes = new byte[count];
            System.arraycopy(bytes, 0, newBytes, 0, count);
            return newBytes;
        }
        return bytes;
    }

    /**
     * 将InputStream输入流转换为字符串
     *
     * @param is 输入流
     * @param charset 字符编码
     * @return 字符串
     * @throws IOException 捕获异常
     */
    public static String toString(InputStream is, String charset) throws IOException {
        byte[] bytes = readBytes(is);
        return new String(bytes, 0, bytes.length, charset);
    }

    /**
     * 将InputStream输入流转换为字符串
     *
     * @param is 输入字节流
     * @param charset 字符编码
     * @return 将输入字节流转换为字符串
     * @throws IOException
     */
    public static String toString(InputStream is, java.nio.charset.Charset charset) throws IOException {
        byte[] bytes = readBytes(is);
        return new String(bytes, 0, bytes.length, charset);
    }

    /**
     * 转换输入字节流为字符流
     *
     * @param is 输入字节流
     * @param charset 字符编码
     * @return 输入字符流
     * @throws UnsupportedEncodingException
     */
    public static BufferedReader toBufferedReader(InputStream is, final String charset) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(is, charset));
    }

    /**
     * 转换输入字节流为字符流
     *
     * @param is 输入字节流
     * @param charset 字符编码
     * @return 输入字符流
     * @throws UnsupportedEncodingException
     */
    public static BufferedReader toBufferedReader(InputStream is, final java.nio.charset.Charset charset) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(is, charset));
    }

    /**
     * 读取数据中的行
     *
     * @param reader 输入字符流
     * @param action 字符串行数据访问器
     * @throws IOException
     */
    public static void readLines(final BufferedReader reader, Consumer<String> action) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            action.accept(line);
            line = reader.readLine();
        }
    }

}
