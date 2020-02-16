package com.kaka.fsm;

import com.kaka.notice.Message;

/**
 * @param <E> 状态机
 */
public interface State<E extends StateMachine> {

    /**
     * 状态进入时执行
     *
     * @param stateMachine 状态机
     */
    void enter(E stateMachine);

    /**
     * 状态的常规更新
     *
     * @param stateMachine 状态机
     */
    void update(E stateMachine);

    /**
     * 状态退出时执行
     *
     * @param stateMachine 状态机
     */
    void exit(E stateMachine);

    /**
     * 当实体处于本状态且接收到事件时，此方法将被执行判断是否进入下一个状态<br>
     * 状态变迁过渡也可在此实施。
     *
     * @param stateMachine 状态机
     * @param message      实体所接收的事件，亦表示状态改变的条件
     * @return true 事件被成功处理;
     */
    boolean onMessage(E stateMachine, Message message);
}
