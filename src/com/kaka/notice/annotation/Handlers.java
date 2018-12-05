package com.kaka.notice.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 核心事件通知框架中的控制器（业务处理器）{@link com.kaka.notice.Command}标注类型
 * 
 * @author zkpursuit
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Handlers {

    public Handler[] value();
}
