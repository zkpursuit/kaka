package com.kaka.notice.register;

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
public class ModelRegister implements IRegister {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ModelRegister.class.getTypeName());

    @Override
    public String name() {
        return "model";
    }

    /**
     * 注册业务模型，业务模型都为单例
     *
     * @param cls 待注册的类
     * @return 注册后的{@link Proxy}
     */
    @Override
    public Object regist(Class<?> cls) {
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
            proxy = cotx.registProxy((Class<? extends Proxy>) cls, modelName);
            logger.log(Level.INFO, "注册业务数据模型：Proxy（{0}）==>>>  {1}", new Object[]{proxy.name, cls});
        } else {
            proxy = cotx.registProxy((Class<? extends Proxy>) cls);
            logger.log(Level.INFO, "注册业务数据模型：Proxy（{0}）==>>>  {1}", new Object[]{proxy.name, cls});
        }
        return proxy;
    }

}
