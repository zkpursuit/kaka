package com.kaka.notice.register;

/**
 * 事件通知注册器
 *
 * @author zkpursuit
 */
public interface IRegister {

    /**
     * 注册器的名称
     *
     * @return 注册器名称
     */
    String name();

    /**
     * 注册事件通知
     *
     * @param cls 事件通知类
     * @return
     */
    Object regist(Class<?> cls);
}
