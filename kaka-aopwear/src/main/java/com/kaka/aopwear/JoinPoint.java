package com.kaka.aopwear;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.sf.cglib.proxy.MethodProxy;

/**
 * AOP连接点
 *
 * @author zkpursuit
 */
public class JoinPoint {

    /**
     * 切面通知Advice被执行时写入的参数
     */
    private Map<String, Object> params;
    /**
     * 方法代理
     */
    MethodProxy proxy;
    /**
     * 被切面代理的对象，实为CgLib创建的对象
     */
    Object target;
    /**
     * 被切面代理的方法
     */
    Method method;
    /**
     * 方法参数
     */
    Object[] args;
    /**
     * 方法执行结果
     */
    Object result;

    /**
     * 私有构造方法，不允许外部创建
     */
    JoinPoint() {
    }

    /**
     * 写入参数键值对
     *
     * @param name 参数名
     * @param value 参数值
     */
    public void setParam(String name, Object value) {
        synchronized (this) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(name, value);
        }
    }

    /**
     * 获得参数值
     *
     * @param name 参数名
     * @return 参数值
     */
    public Object getParam(String name) {
        synchronized (this) {
            if (params == null) {
                return null;
            }
            return params.get(name);
        }
    }

    /**
     * @return 被切面代理的目标对象
     */
    public Object getTarget() {
        return target;
    }

    /**
     * @return 被切面代理的目标方法
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return 被切面代理的目标方法中的参数
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @return 被切面代理的目标方法执行结果
     */
    public Object getResult() {
        return result;
    }

}
