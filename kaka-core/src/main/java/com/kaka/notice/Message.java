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

    /**
     * 初始化设置事件通知处理结果
     *
     * @param <T> 数据类型限定
     * @param name 因广播事件通知，必须为处理结果定义唯一标识名
     * @param result 处理结果数据容器
     * @return 处理结果数据容器
     */
    public <T> IResult<T> setResult(String name, IResult<T> result) {
        synchronized (this) {
            if (this.resultMap == null) {
                this.resultMap = new ConcurrentHashMap<>();
            }
            this.resultMap.put(name, result);
        }
        return result;
    }

    /**
     * 获取处理结果数据容器
     *
     * @param <T> 数据类型限定
     * @param name 因广播事件通知，必须为处理结果定义唯一标识名
     * @return 处理结果数据容器
     */
    public <T> IResult<T> getResult(String name) {
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
