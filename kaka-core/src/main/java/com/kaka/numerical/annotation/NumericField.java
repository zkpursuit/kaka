package com.kaka.numerical.annotation;

import java.lang.annotation.*;

/**
 * 数值配置文件序列化的对象中字段注解，标示字段的值需特殊处理后获得<br>
 *
 * @author zhoukai
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumericField {

    /**
     * 待处理的节点元素
     *
     * @return 节点元素集合
     */
    public String[] elements();

    /**
     * elements中单个节点处理器
     *
     * @return 处理器类
     */
    public Class<? extends Converter> converter() default Converter.class;

    /**
     * 每个字段的处理器
     *
     * @param <T> 处理后的数据，如为数组，则其中的数据将被逐个添加到集合对象中，非数组则整个添加到集合对象中
     */
    public static interface Converter<T> {

        /**
         * 数据转型
         *
         * @param value 原始字符串数据
         * @return 转型后的数据
         */
        T transform(String value);

    }

}
