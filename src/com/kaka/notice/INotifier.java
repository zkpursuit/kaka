package com.kaka.notice;

/**
 * 消息通知发送接口
 *
 * @author zkpursuit
 */
public interface INotifier {

    /**
     * 发送消息通知
     *
     * @param msg 待发送的消息
     */
    void sendMessage(Message msg);

    /**
     * 是否用线程异步处理
     *
     * @param msg 待发送的消息
     * @param asyn true为异步，false为同步
     */
    void sendMessage(Message msg, boolean asyn);
}
