package com.kaka.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不论方法是否正常执行完还是异常退出
 *
 * @author zkpursuit
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {

    /**
     * pointcut表达式 <br>
     * 仅支持以下几种情况：<br>
     * 1、包名.类名.方法名() <br>
     * 2、包名.*.方法名() <br>
     * <br>
     * 以上方法名中小括号内支持以下情况 <br>
     * 1、"..."表示任意参数 <br>
     * 2、空白无任何字符表示仅适配无参数的方法 <br>
     * 3、多个以英文逗号间隔的类型表示适配指定参数个数及类型的方法 <br>
     *
     * @return
     */
    String value();

    /**
     * @return 切面方法执行顺序，数字越大，执行优先级越高
     */
    int order() default 0;
}
