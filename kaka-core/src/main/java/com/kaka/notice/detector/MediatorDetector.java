package com.kaka.notice.detector;

import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Mediator;
import com.kaka.notice.annotation.MultiHandler;

import java.util.logging.Logger;

/**
 * 注册事件观察者
 *
 * @author zkpursuit
 */
public class MediatorDetector implements IDetector {

    private static final Logger logger = Logger.getLogger(MediatorDetector.class.getTypeName());

    @Override
    public String name() {
        return "mediator";
    }

    /**
     * 识别事件观察者相关的类并注册到{@link com.kaka.notice.Facade}
     *
     * @param cls 待识别的类，{@link com.kaka.notice.Mediator}子类
     * @return 是否被识别注册
     */
    @Override
    public boolean discern(Class<?> cls) {
        if (!Mediator.class.isAssignableFrom(cls)) {
            return false;
        }
        MultiHandler sc = cls.getAnnotation(MultiHandler.class);
        if (sc == null) {
            return false;
        }
        Facade cotx;
        if (sc.context().equals("")) {
            cotx = FacadeFactory.getFacade();
        } else {
            cotx = FacadeFactory.getFacade(sc.context());
        }
        Mediator observer = cotx.registerMediator((Class<? extends Mediator>) cls);
        logger.info("注册事件观察者：Mediator（" + observer.name + "）==>>>  " + cls);
        return true;
    }

}
