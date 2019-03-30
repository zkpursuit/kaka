package com.kaka.notice;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 可添加子命令的控制命令类
 *
 * @author zkpursuit
 */
abstract public class MacroCommand extends Command {

    /**
     * 子命令存储容器
     */
    private Collection<Command> subCommands;

    /**
     * 构造方法
     */
    public MacroCommand() {
        this.subCommands = new ArrayList<>();
        this.__init();
    }

    /**
     * 初始化，间接调用添加所有子命令的方法
     */
    private void __init() {
        initializeMacroCommand();
    }

    /**
     * 必须在子类中实现此方法，并在方法体中调用addSubCommand方法添加子命令
     */
    abstract protected void initializeMacroCommand();

    /**
     * 添加子命令
     *
     * @param commandClassRef 子命令
     */
    final protected void addSubCommand(Command commandClassRef) {
        this.subCommands.add(commandClassRef);
    }

    /**
     * 执行所有子命令
     *
     * @param msg 事件消息
     */
    @Override
    public void execute(Message msg) {
        subCommands.stream().map((command) -> {
            command.cmd = cmd;
            command.facade = facade;
            return command;
        }).forEach((command) -> {
            command.execute(msg);
        });
    }

}