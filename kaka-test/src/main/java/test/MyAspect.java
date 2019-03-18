package test;

import com.kaka.aopwear.JoinPoint;
import com.kaka.aopwear.ProceedJoinPoint;
import com.kaka.aop.annotation.After;
import com.kaka.aop.annotation.AfterReturning;
import com.kaka.aop.annotation.AfterThrowing;
import com.kaka.aop.annotation.Around;
import com.kaka.aop.annotation.Aspect;
import com.kaka.aop.annotation.Before;

/**
 *
 * @author zkpursuit
 */
@Aspect
public class MyAspect {
    
    @Before(value = "test.AopCommand.execute(...)", order = 1)
    public void before1(JoinPoint joinPoint) {
        System.out.println("before1");
    }
    
    @Before(value = "test.AopCommand.execute(...)", order = 2)
    public void before2(JoinPoint joinPoint) {
        System.out.println("before2");
    }
    
    @After("test.AopCommand.execute(...)")
    public void after(JoinPoint joinPoint) {
        System.out.println("after");
    }
    
    @Around("test.AopCommand.execute(...)")
    public void around(ProceedJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        joinPoint.proceed();
        System.out.println("around after");
    }
    
    @AfterThrowing("test.AopCommand.execute(...)")
    public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {
        System.out.println("afterThrowing：" + throwable);
    }
    
    @AfterReturning("test.AopCommand.execute(...)")
    public void afterReturning(JoinPoint joinPoint) {
        System.out.println("afterReturning：" + joinPoint.getResult());
    }
    
}
