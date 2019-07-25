package com.kaka.net.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP URL路由标注类型
 *
 * @author zkpursuit
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface WebServlet {

    /**
     * 名称
     *
     * @return
     */
    String name() default "";

    /**
     * 映射的访问路径
     *
     * @return
     */
    String url();

    /**
     * 初始化参数
     *
     * @return
     */
    WebInitParam[] initParams() default {};

    /**
     * 描述
     *
     * @return
     */
    String description() default "";

}
