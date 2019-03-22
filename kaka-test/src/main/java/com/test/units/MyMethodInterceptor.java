/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.units;

import com.kaka.aop.MethodInterceptor;
import com.kaka.aop.MethodInvocation;
import com.kaka.aopwear.ProceedJoinPoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zkpursuit
 */
public class MyMethodInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(MethodInvocation pjp) {
        System.out.println("Intercept Before");
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        System.out.println("Intercept After");
        return result;
    }
}
