package com.kaka.notice;

/**
 * 消息通知者
 *
 * @author zkpursuit
 */
abstract public class Notifier implements INotifier {

    /**
     * 消息中央调度器
     */
    Facade facade;

    /**
     * 同步发送消息
     *
     * @param msg 待发送的消息
     */
    @Override
    public final void sendMessage(Message msg) {
        if (facade != null) {
            facade.sendMessage(msg);
        }
    }

    /**
     * 发送消息
     *
     * @param msg 待发送的消息
     */
    @Override
    public final void sendMessage(Message msg, boolean asyn) {
        if (facade != null) {
            facade.sendMessage(msg, asyn);
        }
    }

    /**
     * 定时调度执行事件通知
     *
     * @param msg 事件
     * @param scheduler 定时调度器
     */
    @Override
    public void sendMessage(Message msg, Scheduler scheduler) {
        if (facade != null) {
            facade.sendMessage(msg, scheduler);
        }
    }

    /**
     * 取消调度
     *
     * @param cmd 事件名
     * @param group 调度器组名
     */
    @Override
    public void cancelSchedule(Object cmd, String group) {
        if (facade != null) {
            facade.cancelSchedule(cmd, group);
        }
    }

    /**
     * 获取通知派发器
     *
     * @return 通知派发器
     */
    protected Facade getFacade() {
        return this.facade;
    }

    /**
     * 获取数据模型
     *
     * @param <T> 类型限定
     * @param name 数据模型名
     * @return 数据模型
     */
    protected <T extends Proxy> T retrieveProxy(String name) {
        if (facade == null) {
            return null;
        }
        return this.facade.retrieveProxy(name);
    }

    /**
     * 获取数据模型
     *
     * @param <T> 类型限定
     * @param cls 数据模型类型
     * @return 数据模型
     */
    protected <T extends Proxy> T retrieveProxy(Class<T> cls) {
        if (facade == null) {
            return null;
        }
        return this.facade.retrieveProxy(cls);
    }

    /**
     * 获取事件观察者
     *
     * @param <T> 类型限定
     * @param mediatorName 事件观察者唯一标识
     * @return 事件观察者
     */
    protected <T extends Mediator> T retrieveMediator(String mediatorName) {
        if (facade == null) {
            return null;
        }
        return this.facade.retrieveMediator(mediatorName);
    }

    /**
     * 获取事件观察者
     *
     * @param <T> 类型限定
     * @param mediatorClass 事件观察者类型
     * @return 事件观察者
     */
    protected <T extends Mediator> T retrieveMediator(Class<T> mediatorClass) {
        if (facade == null) {
            return null;
        }
        return this.facade.retrieveMediator(mediatorClass);
    }

}
