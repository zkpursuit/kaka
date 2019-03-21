package com.kaka.aopwear;

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 切面处理器
 *
 * @author zkpursuit
 */
public class MethodAspectHandler implements MethodInterceptor {

    protected CglibAop aop;

    public MethodAspectHandler(CglibAop aop) {
        this.aop = aop;
    }

    protected ProceedJoinPoint createJoinPoint(ProceedJoinPoint joinPoint, Object target, Method method, Object[] args, MethodProxy proxy, Object result) {
        if (joinPoint == null) {
            joinPoint = new ProceedJoinPoint(target, method, args, proxy, result);
        } else {
            joinPoint.target = target;
            joinPoint.method = method;
            joinPoint.args = args;
            joinPoint.proxy = proxy;
            joinPoint.result = result;
        }
        return joinPoint;
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
//        Class<?> clasz = object.getClass().getSuperclass();
//        if (!aop.class_aspect_method_map.containsKey(clasz)) {
//            return proxy.invokeSuper(object, args);
//        }
//        Map<Method, MethodAdvices> methodMap = aop.class_aspect_method_map.get(clasz);
//        if (!methodMap.containsKey(method)) {
//            return proxy.invokeSuper(object, args);
//        }
        String methodGenName = method.toGenericString();
        if (!aop.method_advices_map.containsKey(methodGenName)) {
            return proxy.invokeSuper(object, args);
        }
        MethodAdvices bag = aop.method_advices_map.get(methodGenName);
        ProceedJoinPoint joinPoint = null;
        if (bag.before != null && !bag.before.isEmpty()) {
            joinPoint = createJoinPoint(joinPoint, object, method, args, proxy, null);
            for (MethodWrap wrap : bag.before) {
                wrap.method.invoke(wrap.object, joinPoint);
            }
        }
        Object result = null;
        try {
            if (bag.around != null && !bag.around.isEmpty()) {
                joinPoint = createJoinPoint(joinPoint, object, method, args, proxy, null);
                for (MethodWrap wrap : bag.around) {
                    result = wrap.method.invoke(wrap.object, joinPoint);
                    joinPoint.result = result;
                }
            } else {
                result = proxy.invokeSuper(object, args);
            }
            if (bag.afterReturning != null && !bag.afterReturning.isEmpty()) {
                joinPoint = createJoinPoint(joinPoint, object, method, args, proxy, result);
                int size = bag.afterReturning.size();
                for (int i = size - 1; i >= 0; i--) {
                    MethodWrap wrap = bag.afterReturning.get(i);
                    wrap.method.invoke(wrap.object, joinPoint);
                }
            }
        } catch (Exception ex) {
            if (bag.afterThrowing != null && !bag.afterThrowing.isEmpty()) {
                joinPoint = createJoinPoint(joinPoint, object, method, args, proxy, result);
                for (MethodWrap wrap : bag.afterThrowing) {
                    wrap.method.invoke(wrap.object, joinPoint, ex);
                }
            } else {
                throw ex;
            }
        } finally {
            if (bag.after != null && !bag.after.isEmpty()) {
                joinPoint = createJoinPoint(joinPoint, object, method, args, proxy, result);
                int size = bag.after.size();
                for (int i = size - 1; i >= 0; i--) {
                    MethodWrap wrap = bag.after.get(i);
                    wrap.method.invoke(wrap.object, joinPoint);
                }
            }
        }
        return result;
    }

}
