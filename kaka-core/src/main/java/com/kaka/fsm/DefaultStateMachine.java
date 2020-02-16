package com.kaka.fsm;

import com.kaka.notice.Message;

/**
 * 默认状态机实现
 *
 * @param <E> 状态机拥有者类型
 * @param <S> 状态机所有状态类型
 */
public class DefaultStateMachine<E, S extends State<StateMachine>> implements StateMachine<E, S> {

    /**
     * 状态机所属实体
     */
    protected E owner;

    /**
     * 状态机所属实体的当前状态
     */
    protected S currentState;

    /**
     * 状态机所属实体的上一个状态
     */
    protected S previousState;

    /**
     * 创建一个默认状态机实例
     */
    public DefaultStateMachine() {
        this(null);
    }

    /**
     * 创建一个默认状态机实例
     *
     * @param owner the owner of the state machine
     */
    public DefaultStateMachine(E owner) {
        this(owner, null);
    }

    /**
     * 创建一个默认状态机实例
     *
     * @param owner        状态机所属实体
     * @param initialState 初始化状态
     */
    public DefaultStateMachine(E owner, S initialState) {
        this.owner = owner;
        this.setInitialState(initialState);
    }

    /**
     * 获得状态机所属实体
     */
    public E getOwner() {
        return owner;
    }

    @Override
    public void setInitialState(S state) {
        this.previousState = null;
        this.currentState = state;
    }

    /**
     * 获取状态机的当前状态
     */
    @Override
    public S getCurrentState() {
        return currentState;
    }

    /**
     * 获取状态机的上一状态
     */
    @Override
    public S getPreviousState() {
        return previousState;
    }

    /**
     * 更新状态机
     * <p>
     * 必须调用当前状态的update方法
     * </p>
     */
    @Override
    public void update() {
        if (currentState != null) currentState.update(this);
    }

    /**
     * 转换到指定状态
     *
     * @param newState 新的目标状态
     */
    @Override
    public void changeState(S newState) {
        previousState = currentState;
        if (currentState != null) currentState.exit(this);
        currentState = newState;
        if (currentState != null) currentState.enter(this);
    }

    /**
     * 回溯到上一个状态
     *
     * @return true表示正确回溯到上一状态；如果没有上一状态，则直接返回false。
     */
    @Override
    public boolean revertToPreviousState() {
        if (previousState == null) {
            return false;
        }
        changeState(previousState);
        return true;
    }

    /**
     * 判断状态机是否处于指定状态
     * <p>
     * 一般我们定义的状态为枚举类型，所以比较实用 {@code ==} 代替 {@code equals} 方法
     * </p>
     *
     * @param state 与当前状态比较的目标状态
     * @return true 表示处于指定状态
     */
    @Override
    public boolean isInState(S state) {
        return currentState == state;
    }

    /**
     * 处理接收到的事件消息，即状态改变的条件
     * <p>
     * 基于此接口的类必须能正确的路由到对应的事件
     * </p>
     *
     * @param message 接收到的事件消息
     * @return true表示事件被成功处理
     */
    @Override
    public boolean handleMessage(Message message) {
        if (currentState != null && currentState.onMessage(this, message)) {
            return true;
        }
        return false;
    }
}
