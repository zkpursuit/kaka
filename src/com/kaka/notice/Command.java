package com.kaka.notice;

import com.kaka.util.ObjectPool;

/**
 * 控制命令类
 *
 * @author zkpursuit
 */
abstract public class Command extends Notifier implements ObjectPool.Poolable {

    /**
     * 注册时的命令号
     */
    Object cmd;

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

    /**
     * 处理消息，此方法中不要用线程处理
     * <br>
     * 由于{@link com.kaka.notice.Facade}中调度事件时，此方法被执行后即刻
     * 对本对象进行池化处理，将调用reset方法；如内部再使用线程，将引发不必要的错误。
     * <br>
     * 未池化的对象因不会调用reset方法，内部可使用线程。
     *
     * @see Message Message处理方法
     * @param msg 通知消息
     */
    abstract public void execute(Message msg);

}
