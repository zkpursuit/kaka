package com.kaka.notice.detector;

import com.kaka.notice.Facade;
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
     * @param cls 待识别的类
     * @return 注册后的{@link com.kaka.notice.Mediator}
     */
    @Override
    public Object discern(Class<?> cls) {
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
