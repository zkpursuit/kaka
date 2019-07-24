package com.kaka.notice.detector;

import com.kaka.notice.Command;
import com.kaka.notice.Facade;
import com.kaka.notice.annotation.Handler;
import com.kaka.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 基于{@link com.kaka.notice.Command}的注册器
 *
 * @author zkpursuit
 */
public class CommandDetector implements IDetector {

    private static final Logger logger = Logger.getLogger(CommandDetector.class.getTypeName());

    @Override
    public String name() {
        return "command";
    }

    /**
     * 识别业务处理器相关的类并注册到{@link com.kaka.notice.Facade}
     *
     * @param cls 待识别的类
     * @return 注册后的 {@link com.kaka.notice.Command}
     */
    @Override
    public Object discern(Class<?> cls) {
        if (!Command.class.isAssignableFrom(cls)) {
            return null;
        }
        Handler[] controllers = cls.getAnnotationsByType(Handler.class);
        if (controllers == null) {
            return null;
        }
        if (controllers.length == 0) {
            return null;
        }
        for (Handler regist : controllers) {
            Object cmd = regist.cmd();
            Class<?> cmdCls = regist.type();
            if (Command.class.isAssignableFrom(cls)) {
                Facade cotx;
                if (regist.context().equals("")) {
                    cotx = Facade.facade;
                } else {
                    cotx = Facade.getInstance(regist.context());
                }
                if (cmdCls != String.class) {
                    String cmdStr = String.valueOf(cmd);
                    if (StringUtils.isNumeric(cmdStr)) {
                        if (cmdCls == short.class || cmdCls == Short.class) {
                            cotx.registerCommand(Short.parseShort(cmdStr), (Class<Command>) cls, regist.pooledSize());
                        } else if (cmdCls == int.class || cmdCls == Integer.class) {
                            cotx.registerCommand(Integer.parseInt(cmdStr), (Class<Command>) cls, regist.pooledSize());
                        } else if (cmdCls == long.class || cmdCls == Long.class) {
                            cotx.registerCommand(Long.parseLong(cmdStr), (Class<Command>) cls, regist.pooledSize());
                        }
                    } else {
                        cotx.registerCommand(cmd, (Class<Command>) cls, regist.pooledSize());
                        logger.log(Level.WARNING, "注解cmd数据类型与注解type参数描述的类型不一致，强制以cmd参数类型注册！{0}", new Object[]{cls});
                    }
                    logger.log(Level.INFO, "注册业务处理器：cmd（{0}）：{1}  ==>>>  {2}", new Object[]{cmdCls.getTypeName(), regist.cmd(), cls});
                } else {
                    cotx.registerCommand(cmd, (Class<Command>) cls, regist.pooledSize());
                    logger.log(Level.INFO, "注册业务处理器：cmd（{0}）：{1}  ==>>>  {2}", new Object[]{cmdCls.getTypeName(), regist.cmd(), cls});
                }
            }
        }
        return null;
    }

}
