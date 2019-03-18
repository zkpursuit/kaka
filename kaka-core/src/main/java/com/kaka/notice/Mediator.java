package com.kaka.notice;

/**
 * 消息观察者，感知有兴趣的消息通知
 *
 * @author zkpursuit
 */
abstract public class Mediator extends Proxy {

    /**
     * 构造方法
     */
    public Mediator() {
        super();
    }

    /**
     * 构造方法
     *
     * @param name 实例名
     */
    public Mediator(String name) {
        super(name);
    }

    /**
     * 处理消息监听
     *
     * @param msg 通知消息
     */
    abstract public void handleMessage(Message msg);

    /**
     * 监听的消息列表，非Event实现
     *
     * @return 感兴趣的消息通知集合
     */
    abstract public Object[] listMessageInterests();

}
