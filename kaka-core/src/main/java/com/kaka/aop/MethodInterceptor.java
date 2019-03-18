package com.kaka.aop;

/**
 * 方法拦截执行器
 *
 * @author zkpursuit
 */
public interface MethodInterceptor {

    Object intercept(MethodInvocation pjp);

}
