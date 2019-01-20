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

    public boolean isDone() {
        return this.result != NULL;
    }

    @Override
    public V get() {
//        synchronized (this) {
//            while (!isDone()) {
//                try {
//                    this.wait();
//                } catch (InterruptedException ex) {
//                    throw new Error(ex);
//                }
//            }
//        }
//        return (V) this.result;
        try {
            return get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            return null;
        }
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException {
        if (await(timeout, unit)) {
            return (V) this.result;
        }
        return null;
    }

    private boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        timeout = unit.convert(timeout, TimeUnit.MILLISECONDS);
        long startTime = timeout <= 0 ? 0 : System.currentTimeMillis();
        long waitTime = timeout;
        synchronized (this) {
            for (;;) {
                if (isDone()) {
                    return true;
                }
                if (waitTime <= 0) {
                    return isDone();
                }
                this.wait(waitTime, 999999);
                waitTime = timeout - (System.currentTimeMillis() - startTime);
                if (waitTime <= 0) {
                    return isDone();
                }
            }
        }
    }

    @Override
    public void set(V result) {
        synchronized (this) {
            this.result = result;
            this.notifyAll();
        }
    }

}
