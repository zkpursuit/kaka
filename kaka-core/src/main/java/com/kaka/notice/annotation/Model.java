package com.kaka.notice.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 核心事件通知框架中的数据模型标注类型{@link com.kaka.notice.Proxy}
 *
 * @author zkpursuit
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Model {

    /**
     * 数据模型唯一名，默认为空（表示核心不以此名称注册数据模型）
     *
     * @return 数据模型唯一名
     */
    String value() default "";
    
    /**
     * 被注册到的目标{@link com.kaka.notice.Facade}唯一名
     * 
     * @return {@link com.kaka.notice.Facade}唯一名
     */
    String context() default "";
}
