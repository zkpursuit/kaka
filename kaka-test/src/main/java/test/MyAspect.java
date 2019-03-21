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
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> before1");
    }
    
    @Before(value = "test.AopCommand.execute(...)", order = 2)
    public void before2(JoinPoint joinPoint) {
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> before2");
    }
    
    @After("test.AopCommand.execute(com.kaka.notice.Message)")
    public void after(JoinPoint joinPoint) {
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> after");
    }
    
    @Around("test.AopCommand.execute(com.kaka.notice.Message)")
    public void around(ProceedJoinPoint joinPoint) throws Throwable {
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> around before");
        joinPoint.proceed();
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> around after");
    }
    
    @AfterThrowing("test.AopCommand.execute(...)")
    public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> afterThrowing：" + throwable);
    }
    
    @AfterReturning("test.AopCommand.execute(...)")
    public void afterReturning(JoinPoint joinPoint) {
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> afterReturning：" + joinPoint.getResult());
    }

    @Before("test.MyObject.set(...)")
    public void before(JoinPoint joinPoint) {
        System.out.println(joinPoint.getMethod().toGenericString() + " ===>> before");
    }
    
}
