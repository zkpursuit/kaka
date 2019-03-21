package com.kaka.aopwear;

import java.lang.reflect.Method;

import com.kaka.aop.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 被代理对象的方法拦截处理器
 *
 * @author zkpursuit
 */
public class MethodInterceptHandler extends MethodAspectHandler {

    public MethodInterceptHandler(CglibAop aop) {
        super(aop);
    }

    /**
     * @param object cglib创建的对象
     * @param method 被cglib代理拦截的原始方法
     * @param args   方法参数
     * @param proxy  cglib代理
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodGenName = method.toGenericString();
        if (!aop.class_method_interceptor_map.containsKey(methodGenName)) {
            return proxy.invokeSuper(object, args);
        }
        MethodInterceptor interceptor = aop.class_method_interceptor_map.get(methodGenName);
        ProceedJoinPoint joinPoint = createJoinPoint(null, object, method, args, proxy, null);
        return interceptor.intercept(joinPoint);
    }

}
