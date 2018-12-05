package com.kaka.util;

/**
 * 数组操作相关工具类
 *
 * @author zkpursuit
 */
public final class ArrayUtils {

    /**
     * 数组首尾反转
     *
     * @param <T> 数组类型
     * @param array 数组对象
     * @param startIndexInclusive 反转的开始索引
     * @param endIndexExclusive 反转的结束索引 + 1
     * @return 翻转后的原数组
     */
    public final static <T> T[] reverse(final T[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        T tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 数组反转
     *
     * @param <T> 数组类型
     * @param array 数组对象
     */
    public final static <T> void reverse(final T[] array) {
        reverse(array, 0, array.length);
    }

    /**
     * 数组首尾反转
     *
     * @param array 数组对象
     * @param startIndexInclusive 反转的开始索引
     * @param endIndexExclusive 反转的结束索引+1
     * @return 翻转后的原数组
     */
    public final static Object reverse(final Object array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        int length = getLength(array);
        if (length == 0) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j;
        if (endIndexExclusive < 0) {
            j = length - 1;
        } else {
            j = Math.min(length, endIndexExclusive) - 1;
        }
        Object tmp;
        while (j > i) {
            tmp = get(array, j);
            set(array, j, get(array, i));
            set(array, i, tmp);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 数组反转
     *
     * @param array 数组对象
     * @return 翻转后的原数组
     */
    public final static Object reverse(final Object array) {
        return reverse(array, 0, -1);
    }

    /**
     * 创建一个新的数组
     *
     * @param c 数组元素类型
     * @param size 数组大小
     * @return 新的数组
     */
    final static public Object newInstance(Class<?> c, int size) {
        return java.lang.reflect.Array.newInstance(c, size);
    }

    /**
     * 创建一个新的数组
     *
     * @param <T> 数组类型
     * @param c 数组类型
     * @param size 数组初始长度
     * @return 数组对象
     */
    final static public <T> T[] newObjectArray(Class<T> c, int size) {
        return (T[]) java.lang.reflect.Array.newInstance(c, size);
    }

    /**
     * 获取数组的长度
     *
     * @param array 数组对象
     * @return 数组长度
     */
    final static public int getLength(Object array) {
        return java.lang.reflect.Array.getLength(array);
    }

    /**
     * 获取数组类型
     *
     * @param array 数组对象
     * @return 数组元素类型
     */
    final static public Class getElementType(Object array) {
        return array.getClass().getComponentType();
    }

    /**
     * 获取数组类型
     *
     * @param array 数组对象
     * @return 数组元素类型通用名
     */
    final static public String getElementTypeGenericName(Object array) {
        return getElementType(array).toGenericString();
    }

    /**
     * 获取数组指定元素的值
     *
     * @param array 数组对象
     * @param index 指定的数组下标
     * @return 指定的元素值
     */
    final static public Object get(Object array, int index) {
        return java.lang.reflect.Array.get(array, index);
    }

    /**
     * 设置数组指定位置处的值
     *
     * @param array 数组对象
     * @param index 数组下标
     * @param value 赋入的元素值
     */
    final static public void set(Object array, int index, Object value) {
        java.lang.reflect.Array.set(array, index, value);
    }

}
