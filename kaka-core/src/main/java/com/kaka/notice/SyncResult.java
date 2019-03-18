package com.kaka.notice;

/**
 * 同步处理结果
 *
 * @author zkpursuit
 * @param <V>
 */
public class SyncResult<V> implements IResult<V> {

    private volatile V result;

    /**
     * 赋值事件处理结果
     *
     * @param result 事件处理结果
     */
    @Override
    public void set(V result) {
        this.result = result;
    }

    /**
     * 获取事件处理结果
     *
     * @return 事件处理结果
     */
    @Override
    public V get() {
        return this.result;
    }

}
