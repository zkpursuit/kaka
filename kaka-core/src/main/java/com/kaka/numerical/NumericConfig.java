package com.kaka.numerical;

import com.kaka.notice.Proxy;
import com.kaka.util.ReflectUtils;

/**
 * 数值表配置
 *
 * @param <T> 解析成为的目标类型
 * @author zhoukai
 */
abstract public class NumericConfig<T> extends Proxy {

    /**
     * 将文件数据转换为JavaBean对象
     *
     * @param filePath 文件路径
     * @throws Exception 解析异常
     */
    abstract public void parse(String filePath) throws Exception;

    /**
     * 缓存对象
     *
     * @param info 解析序列化后的对象
     */
    abstract protected void cacheObject(T info);

    /**
     * 解析文件之前
     */
    abstract protected void parseBefore();

    /**
     * 文件解析完成后
     */
    abstract protected void parseAfter();

    /**
     * 获取配置映射的类
     *
     * @return 配置映射的类
     */
    final public Class<T> getMappingClass() {
        return (Class<T>) ReflectUtils.getGenericParadigmClass(this.getClass());
    }

}
