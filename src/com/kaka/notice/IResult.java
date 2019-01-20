package com.kaka.notice;

/**
 *
 * @author zkpursuit
 * @param <V>
 */
public interface IResult<V> {
    void set(V result);
    V get();
}
