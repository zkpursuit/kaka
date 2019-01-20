package com.kaka.notice;

/**
 * 同步处理结果
 * 
 * @author zkpursuit
 * @param <V>
 */
public class SyncResult<V> implements IResult<V> {

    private volatile V result;

    @Override
    public void set(V result) {
        this.result = result;
    }

    @Override
    public V get() {
        return this.result;
    }

}
