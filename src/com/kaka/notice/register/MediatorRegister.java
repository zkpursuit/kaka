package com.kaka.notice.register;

import com.kaka.notice.Facade;
import com.kaka.notice.Mediator;
import com.kaka.notice.annotation.MultiHandler;
import java.util.logging.Logger;

/**
 * 注册事件观察者
 *
 * @author zkpursuit
 */
public class MediatorRegister implements IRegister {

    private static final Logger logger = Logger.getLogger(MediatorRegister.class.getTypeName());

    @Override
    public String name() {
        return "mediator";
    }

    @Override
    public Object regist(Class<?> cls) {
        if (!Mediator.class.isAssignableFrom(cls)) {
            return null;
        }
        MultiHandler sc = cls.getAnnotation(MultiHandler.class);
        if (sc == null) {
            return null;
        }
        Facade cotx;
        if (sc.context().equals("")) {
            cotx = Facade.facade;
        } else {
            cotx = Facade.getInstance(sc.context());
        }
        Mediator observer = cotx.registMediator((Class<? extends Mediator>) cls);
        logger.info("注册事件观察者：Mediator（" + observer.name + "）==>>>  " + cls);
        return observer;
    }

}
