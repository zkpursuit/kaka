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
     * 获取通知派发器
     *
     * @return 通知派发器
     */
    protected Facade getDispatcher() {
        return this.facade;
    }

    /**
     * 获取数据模型
     *
     * @param <T> 类型限定
     * @param name 数据模型名
     * @return 数据模型
     */
    protected <T extends Proxy> T getProxy(String name) {
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
    protected <T extends Proxy> T getProxy(Class<T> cls) {
        if (facade == null) {
            return null;
        }
        return this.facade.retrieveProxy(cls);
    }

    /**
     * 获取事件观察者
     *
     * @param <T> 类型限定
     * @param cls 事件观察者类型
     * @return 事件观察者
     */
    protected <T extends Mediator> T getObserver(Class<T> cls) {
        if (facade == null) {
            return null;
        }
        return this.facade.retrieveProxy(cls);
    }

}
