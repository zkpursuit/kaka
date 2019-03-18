package com.kaka.aopwear;

import java.lang.reflect.Method;

import com.kaka.aop.MethodInvocation;
import net.sf.cglib.proxy.MethodProxy;

/**
 *
 * @author zkpursuit
 */
final public class ProceedJoinPoint extends JoinPoint implements MethodInvocation {

    ProceedJoinPoint() {

    }

    ProceedJoinPoint(Object target, Method method, Object[] args, MethodProxy proxy) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.proxy = proxy;
    }

    ProceedJoinPoint(Object target, Method method, Object[] args, MethodProxy proxy, Object result) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.proxy = proxy;
        this.result = result;
    }

    final public Object proceed() throws Throwable {
        return proxy.invokeSuper(target, args);
    }
}
