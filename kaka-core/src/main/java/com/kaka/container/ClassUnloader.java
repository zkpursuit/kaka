package com.kaka.container;

public interface ClassUnloader {

    /**
     * 从来加载器中卸载相关的类引用 <br>
     * 主要用于{@link com.kaka.container.ContextClassLoader}卸载资源 <br>
     * 暂未验证其有效性和健壮性，可忽略对此接口的调用
     *
     * @param loader 类加载器
     */
    void unload(ClassLoader loader);
}
