package com.kaka.notice;

import com.kaka.util.ObjectPool.Poolable;

/**
 * 控制命令类
 *
 * @author zkpursuit
 */
abstract public class Command extends Notifier implements Poolable {

    /**
     * 注册时的命令号
     */
    Object cmd;

    /**
     * 处理消息，此方法中不要用线程处理
     *
     * @see Message Message处理方法
     * @param msg 通知消息
     */
    abstract public void execute(Message msg);

    /**
     * 获取注册时的命令号
     *
     * @return 命令号
     */
    protected Object cmd() {
        return cmd;
    }

    /**
     * 重置对象，以备放入对象池中再次利用
     */
    @Override
    public void reset() {
        this.facade = null;
        this.cmd = null;
    }

}
