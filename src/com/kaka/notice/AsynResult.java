package com.kaka.notice;

import java.util.concurrent.TimeUnit;

/**
 * 异步处理结果
 *
 * @author zkpursuit
 * @param <V>
 */
public class AsynResult<V> implements IResult<V> {

    private final static Object NULL = new Object();
    private volatile Object result = NULL;
    private long defaultWaitMillsecs = 5000;

    /**
     * 构造方法
     *
     * @param defaultWaitMillsecs 默认wait超时时间，单位毫秒
     */
    public AsynResult(long defaultWaitMillsecs) {
        this.defaultWaitMillsecs = defaultWaitMillsecs;
    }
    
    /**
     * 默认构造方法
     */
    public AsynResult() {
        this(5000);
    }

    /**
     * 内部调用，尽可能的防止锁嵌套，避免死锁
     *
     * @return 是否赋值结果
     */
    private boolean _isDone() {
        return this.result != NULL;
    }

    /**
     * 结果是否处理完成
     *
     * @return true处理完成
     */
    public boolean isDone() {
        synchronized (this) {
            return this._isDone();
        }
    }

    /**
     * 获取事件通知处理结果具体数值
     * <br>
     * 默认5秒超时时间，超出5秒返回null
     *
     * @return 处理结果具体数值
     */
    @Override
    public V get() {
        try {
            return get(defaultWaitMillsecs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            return null;
        }
    }

    /**
     * 获取事件通知处理结果具体数值
     *
     * @param timeout 超时时间
     * @param unit 超时时间类型
     * @return 处理结果具体数值
     * @throws InterruptedException
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException {
        if (await(timeout, unit)) {
            return (V) this.result;
        }
        return null;
    }

    /**
     * 等待赋值处理结果
     *
     * @param timeout 超时时间
     * @param unit 超时时间类型
     * @return true 成功赋值处理结果
     * @throws InterruptedException
     */
    private boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        timeout = unit.convert(timeout, TimeUnit.MILLISECONDS);
        long startTime = timeout <= 0 ? 0 : System.currentTimeMillis();
        long waitTime = timeout;
        synchronized (this) {
            for (;;) {
                if (_isDone()) {
                    return true;
                }
                if (waitTime <= 0) {
                    return _isDone();
                }
                this.wait(waitTime, 999999);
                waitTime = timeout - (System.currentTimeMillis() - startTime);
                if (waitTime <= 0) {
                    return _isDone();
                }
            }
        }
    }

    /**
     * 赋值处理结果
     *
     * @param result 事件处理结果
     */
    @Override
    public void set(V result) {
        synchronized (this) {
            this.result = result;
            this.notifyAll();
        }
    }

}
