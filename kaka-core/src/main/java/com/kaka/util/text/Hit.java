package com.kaka.util.text;

/**
 * 命中的关键词访问器
 */
public interface Hit<T> {

    void visit(int begin, int end, T value);

}
