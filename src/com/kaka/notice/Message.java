package com.kaka.notice;

import com.kaka.util.ObjectPool.Poolable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息通知对象
 *
 * @author zkpursuit
 */
public class Message implements Poolable {

    protected Object what;
    protected Object body;
    private Map<Object, IResult> resultMap;

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

    /**
     * 构造方法
     *
     * @param what 消息通知标识
     */
    public Message(Object what) {
        this(what, null);
    }

    public Object getWhat() {
        return this.what;
    }

    public Object getBody() {
        return this.body;
    }

    public IResult setResult(String name, IResult result) {
        synchronized (this) {
            if (this.resultMap == null) {
                this.resultMap = new ConcurrentHashMap<>();
            }
            this.resultMap.put(name, result);
            return result;
        }
    }

    public IResult getResult(String name) {
        synchronized (this) {
            if (this.resultMap == null) {
                return null;
            }
            return this.resultMap.get(name);
        }
    }

    @Override
    public void reset() {
        this.what = null;
        this.body = null;
        if (this.resultMap != null) {
            this.resultMap.clear();
        }
    }

}
