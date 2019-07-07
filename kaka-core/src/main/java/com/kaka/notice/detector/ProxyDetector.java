package com.kaka.notice.detector;

import com.kaka.notice.Facade;
import com.kaka.notice.Mediator;
import com.kaka.notice.Proxy;
import com.kaka.notice.annotation.Model;

import java.util.logging.Level;

/**
 * 基于{@link com.kaka.notice.Proxy}的注册器
 *
 * @author zkpursuit
 */
public class ProxyDetector implements IDetector {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProxyDetector.class.getTypeName());

    @Override
    public String name() {
        return "model";
    }

    /**
     * 识别业务模型相关的类并注册到{@link com.kaka.notice.Facade}
     *
     * @param cls 待识别的类
     * @return 注册后的{@link com.kaka.notice.Proxy}
     */
    @Override
    public Object discern(Class<?> cls) {
        if (!Proxy.class.isAssignableFrom(cls)) {
            return null;
        }
        if (Mediator.class.isAssignableFrom(cls)) {
            return null;
        }
        Model model = cls.getAnnotation(Model.class);
        if (model == null) {
            return null;
        }
        Facade cotx;
        if (model.context().equals("")) {
            cotx = Facade.facade;
        } else {
            cotx = Facade.getInstance(model.context());
        }
        Proxy proxy;
        String modelName = model.value();
        if (!"".equals(modelName)) {
            proxy = cotx.registerProxy((Class<? extends Proxy>) cls, modelName);
            logger.log(Level.INFO, "注册业务数据模型：Proxy（{0}）==>>>  {1}", new Object[]{proxy.name, cls});
        } else {
            proxy = cotx.registerProxy((Class<? extends Proxy>) cls);
            logger.log(Level.INFO, "注册业务数据模型：Proxy（{0}）==>>>  {1}", new Object[]{proxy.name, cls});
        }
        return proxy;
    }

}
