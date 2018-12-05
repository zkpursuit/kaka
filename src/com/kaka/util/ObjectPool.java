package com.kaka.util;

import com.kaka.util.ObjectPool.Poolable;
import java.util.Stack;

/**
 * 对象池
 *
 * @author zhoukai
 * @param <T> 可闲置对象类型
 */
abstract public class ObjectPool<T extends Poolable> {

    /**
     * 可池化的对象最大数量
     */
    public final int max;
    /**
     * 闲置对象数量峰值
     */
    private int peak;

    private Stack<T> freeObjects;

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
            freeObjects = new Stack();
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
        if (freeObjects == null) {
            return newObject();
        }
        return freeObjects.isEmpty() ? newObject() : freeObjects.pop();
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
        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
        if (freeObjects != null && freeObjects.size() < max) {
            freeObjects.add(object);
            peak = Math.max(peak, freeObjects.size());
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
     *
     * @return
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
        public void reset();
    }
}
