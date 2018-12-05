package com.kaka.notice.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 核心事件通知框架中事件观察者{@link com.kaka.notice.Mediator}标注类型
 *
 * @author zkpursuit
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MultiHandler {
    /**
     * 被注册到的目标{@link com.kaka.notice.Facade}唯一名
     * 
     * @return {@link com.kaka.notice.Facade}唯一名
     */
    String context() default "";
}
