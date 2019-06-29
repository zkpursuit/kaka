package com.kaka.numerical.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数值配置表解析器注解
 *
 * @author zkpursuit
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Numeric {

    /**
     * 配置文件名或路径
     *
     * @return
     */
    public String src();
    
    /**
     * 被注册到的目标{@link com.kaka.notice.Facade}唯一名
     * 
     * @return {@link com.kaka.notice.Facade}唯一名
     */
    public String context() default "";

}
