package com.kaka.aopwear;

import java.lang.reflect.Method;

import com.kaka.aop.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 *
 * @author zkpursuit
 */
public class MethodInterceptHandler extends MethodAspectHandler {

    public MethodInterceptHandler(CglibAop aop) {
        super(aop);
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String genName = method.toGenericString();
        if (!aop.class_method_interceptor_map.containsKey(genName)) {
            return proxy.invokeSuper(object, args);
        }
        MethodInterceptor interceptor = aop.class_method_interceptor_map.get(genName);
        ProceedJoinPoint joinPoint = createJoinPoint(null, object, method, args, proxy, null);
        return interceptor.intercept(joinPoint);
    }

}
