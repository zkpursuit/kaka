package com.kaka.aopwear;

import java.lang.reflect.Method;

/**
 *
 * @author zkpursuit
 */
class MethodWrap {

    /**
     * 切面对象
     */
    Object object;
    /**
     * 切面方法
     */
    Method method;
    /**
     * 切面方法执行优先级
     */
    int order;

    /**
     * 构造方法
     *
     * @param object 切面对象
     * @param method 切面方法
     * @param order 切面方法执行优先级
     */
    MethodWrap(Object object, Method method, int order) {
        this.object = object;
        this.method = method;
        this.order = order;
    }

}
