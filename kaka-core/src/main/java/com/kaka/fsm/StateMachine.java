package com.kaka.fsm;

import com.kaka.notice.Message;

/**
 * 状态机管理其实体的状态转换，且实体可以委托状态机处理其消息。<br>
 * 状态机主要由现态、条件、动作、次态四要素组成。<br>
 * 现态：是指当前所处的状态。 <br>
 * 条件：又称为“事件”。当一个条件被满足，将会触发一个动作，或者执行一次状态的迁移。 <br>
 * 动作：条件满足后执行的动作。动作执行完毕后，可以迁移到新的状态，也可以仍旧保持原状态。动作不是必需的，当条件满足后，也可以不执行任何动作，直接迁移到新状态。 <br>
 * 次态：条件满足后要迁往的新状态。“次态”是相对于“现态”而言的，“次态”一旦被激活，就转变成新的“现态”了。 <br>
 *
 * @param <E> 状态机绑定的实体
 * @param <S> 状态机所有状态类型
 */
public interface StateMachine<E, S extends State<StateMachine>> {

    /**
     * 状态机绑定的实体
     *
     * @return 状态机绑定的实体
     */
    E getOwner();

    /**
     * 更新状态机
     * <p>
     * 必须调用当前状态的update方法
     * </p>
     */
    void update();

    /**
     * 转换到指定状态
     *
     * @param newState 新的目标状态
     */
    void changeState(S newState);

    /**
     * 回溯到上一个状态
     *
     * @return true表示正确回溯到上一状态；如果没有上一状态，则直接返回false。
     */
    boolean revertToPreviousState();

    /**
     * 为此状态机设置初始状态
     *
     * @param state 初始状态
     */
    void setInitialState(S state);

    /**
     * 获取状态机的当前状态
     */
    S getCurrentState();

    /**
     * 获取状态机的上一状态
     */
    S getPreviousState();

    /**
     * 判断状态机是否处于指定状态
     *
     * @param state 与当前状态比较的目标状态
     * @return true 表示处于指定状态
     */
    boolean isInState(S state);

    /**
     * 处理接收到的事件消息
     * <p>
     * 基于此接口的类必须能正确的路由到对应的事件
     * </p>
     *
     * @param message 接收到的事件消息
     * @return true表示事件被成功处理
     */
    boolean handleMessage(Message message);
}
