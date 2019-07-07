package com.kaka.aop;

import com.kaka.container.ClassUnloader;

abstract public class Aop implements ClassUnloader {

    /**
     * 注册切面类
     *
     * @param aspectClass
     */
    abstract public void registerAspect(Class<?> aspectClass);

    /**
     * 注册被拦截的对象，此对象中的某些方法包含拦截器注解
     *
     * @param targetClass
     */
    abstract public void registerInterceptTarget(Class<?> targetClass);

    /**
     * 被代理对象是否已准备被代理处置
     *
     * @param clasz
     * @return
     */
    abstract public boolean isPrepared(Class<?> clasz);

    /**
     * 创建被切面代理后的对象
     *
     * @param clasz
     * @param <T>
     * @return
     */
    abstract public <T> T createInstance(Class<? extends T> clasz);

    /**
     * 从类加载器中卸载相关类，此相关类表示受Aop容器管理的类
     *
     * @param loader 类加载器
     */
    abstract public void unload(ClassLoader loader);

}
