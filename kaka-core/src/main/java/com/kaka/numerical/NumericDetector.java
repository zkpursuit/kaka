package com.kaka.numerical;

import com.kaka.notice.Facade;
import com.kaka.notice.Proxy;
import com.kaka.notice.detector.IDetector;
import com.kaka.numerical.annotation.Numeric;
import com.kaka.util.StringUtils;

/**
 * 基于{@link com.kaka.numerical.NumericConfig}的注册器
 *
 * @author zkpursuit
 */
public class NumericDetector implements IDetector {

    @Override
    public String name() {
        return "numeric";
    }

    /**
     * 解析数值配置文件，如果有数值配置文件需要被解析，那么必须覆盖此方法<br>
     * 目前支持解析的配置文件，包括Excel内容直接复制的纯文本数据和XML格式的数据<br>
     *
     * @param cls 待注册的类
     * @return 注册后的{@link NumericConfig}
     */
    @Override
    public Object discern(Class<?> cls) {
        if (!NumericConfig.class.isAssignableFrom(cls)) {
            return null;
        }
        Numeric sc = cls.getAnnotation(Numeric.class);
        if (sc == null) {
            return null;
        }
        if (!StringUtils.isNotEmpty(sc.src())) {
            return null;
        }
        Facade cotx;
        if (sc.context().equals("")) {
            cotx = Facade.facade;
        } else {
            cotx = Facade.getInstance(sc.context());
        }
        return cotx.registerProxy((Class<? extends Proxy>) cls, sc.src());
    }
}
