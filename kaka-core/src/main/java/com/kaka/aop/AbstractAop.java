package com.kaka.aop;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract public class AbstractAop {

    /**
     * 包名对应包下的所有Class
     */
    protected final Map<String, Set<Class<?>>> packageClassMap = new ConcurrentHashMap<>();
    /**
     * 类名对应Class
     */
    protected final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    public void cache(String className, Class<?> clasz) {
        classMap.put(className, clasz);
    }

    public void cache(String packageName, Set<Class<?>> classes) {
        if (packageClassMap.containsKey(packageName)) {
            Set<Class<?>> set = packageClassMap.get(packageName);
            set.addAll(classes);
        } else {
            packageClassMap.put(packageName, classes);
        }
    }

    abstract public boolean isRegistered(Class<?> clasz);

    abstract public void registerAspect(Class<?> aspectClass);

    abstract public void registerInterceptTarget(Class<?> targetClass);

    abstract public Object createInstance(Class clasz);

}
