package com.kaka.notice;

import com.kaka.util.ObjectPool.Poolable;

/**
 * 消息通知对象
 *
 * @author zkpursuit
 */
public class Message implements Poolable {

    protected Object what;
    protected Object body;

    /**
     * 构造方法
     *
     * @param what 消息通知标识
     */
    public Message(Object what) {
        this.what = what;
        this.body = null;
    }

    /**
     * 构造方法
     *
     * @param what 消息通知标识
     * @param body 绑定的数据
     */
    public Message(Object what, Object body) {
        this.what = what;
        this.body = body;
    }

    public Object getWhat() {
        return this.what;
    }

    public Object getBody() {
        return this.body;
    }

    @Override
    public void reset() {
        this.what = null;
        this.body = null;
    }

}
