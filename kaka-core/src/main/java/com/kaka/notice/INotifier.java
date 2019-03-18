package com.kaka.notice;

/**
 * 消息通知发送接口
 *
 * @author zkpursuit
 */
interface INotifier {

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

    /**
     * 定时调度执行事件通知
     *
     * @param msg 事件
     * @param scheduler 定时调度器
     */
    void sendMessage(Message msg, Scheduler scheduler);

    /**
     * 取消调度
     *
     * @param cmd 事件名
     * @param group 调度器组名
     */
    void cancelSchedule(Object cmd, String group);
}
