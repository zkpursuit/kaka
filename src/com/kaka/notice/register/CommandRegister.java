package com.kaka.notice.register;

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
public class CommandRegister implements IRegister {

    private static final Logger logger = Logger.getLogger(CommandRegister.class.getTypeName());

    @Override
    public String name() {
        return "command";
    }

    /**
     * 注册业务处理器
     *
     * @param cls 待注册的类
     * @return 注册后的{@link Command}
     */
    @Override
    public Object regist(Class<?> cls) {
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
                            cotx.registCommand(Short.parseShort(cmdStr), (Class<Command>) cls, regist.pooledSize());
                        } else if (cmdCls == int.class || cmdCls == Integer.class) {
                            cotx.registCommand(Integer.parseInt(cmdStr), (Class<Command>) cls, regist.pooledSize());
                        } else if (cmdCls == long.class || cmdCls == Long.class) {
                            cotx.registCommand(Long.parseLong(cmdStr), (Class<Command>) cls, regist.pooledSize());
                        }
                    } else {
                        cotx.registCommand(cmd, (Class<Command>) cls, regist.pooledSize());
                        logger.log(Level.WARNING, "注解cmd数据类型与注解type参数描述的类型不一致，强制以cmd参数类型注册！{0}", new Object[]{cls});
                    }
                    logger.log(Level.INFO, "注册业务处理器：cmd（{0}）：{1}  ==>>>  {2}", new Object[]{cmdCls.getTypeName(), regist.cmd(), cls});
                } else {
                    cotx.registCommand(cmd, (Class<Command>) cls, regist.pooledSize());
                    logger.log(Level.INFO, "注册业务处理器：cmd（{0}）：{1}  ==>>>  {2}", new Object[]{cmdCls.getTypeName(), regist.cmd(), cls});
                }
            }
        }
        return null;
    }

}
