package com.kaka.util;

import com.kaka.util.ObjectPool.Poolable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 对象池
 *
 * @param <T> 可闲置对象类型
 * @author zhoukai
 */
abstract public class ObjectPool<T extends Poolable> {

    /**
     * 可池化的对象最大数量
     */
    public final int max;
    /**
     * 闲置对象数量峰值
     */
    private volatile int peak;
    /**
     * 对象存储队列
     */
    private Queue<T> freeObjects;

    /**
     * 构造方法
     */
    public ObjectPool() {
        this(Integer.MAX_VALUE);
    }

    /**
     * 构造方法
     *
     * @param max 对象池中可缓存的最大池化对象
     */
    public ObjectPool(int max) {
        this.max = max;
        if (this.max > 0) {
            freeObjects = new ConcurrentLinkedQueue();
        }
    }

    /**
     * 创建新的可池化对象
     *
     * @return 新对象
     */
    abstract protected T newObject();

    /**
     * 从池中取出一个可用对象
     *
     * @return 可用对象
     */
    public T obtain() {
        return (freeObjects == null || freeObjects.isEmpty()) ? newObject() : freeObjects.poll();
    }

    /**
     * 将对象闲置并放入对象池
     *
     * @param object 需要闲置的对象
     */
    public void idle(T object) {
        if (max <= 0) {
            return;
        }
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        object.reset();
        if (freeObjects != null) {
            int idleCount = freeObjects.size();
            if (idleCount < max) {
                freeObjects.add(object);
                peak = Math.max(peak, idleCount + 1);
            }
        }
    }

    /**
     * 清除对象池中的所有数据
     */
    public void clear() {
        if (freeObjects != null) {
            freeObjects.clear();
        }
    }

    /**
     * 获取对象池中的闲置对象总数
     *
     * @return 闲置对象总数
     */
    public int getIdleCount() {
        if (freeObjects != null) {
            return freeObjects.size();
        }
        return 0;
    }

    /**
     * 可池化对象数量的最大值
     *
     * @return
     */
    public int max() {
        return this.max;
    }

    /**
     * 池中闲置对象数量的峰值
     * <br>
     * 此值添加volatile修饰，为了尽可能的增加并发性能，故对此值未加锁控制，因 此不一定完全真实，一般情况，我们不一定用到此值。
     * <br>
     * 如需具有真实的参考，请改写源码自行对此值加锁，建议使用jdk8的StampedLock
     *
     * @return 闲置对象数量的峰值
     */
    public int peak() {
        return this.peak;
    }

    /**
     * 可池化对象接口
     */
    static public interface Poolable {

        /**
         * 重置对象数据，保障下次使用时为初始化状态
         */
        void reset();
    }
}