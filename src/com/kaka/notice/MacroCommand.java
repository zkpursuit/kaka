package com.kaka.notice;

import java.util.Collection;
import java.util.Stack;

/**
 * 可添加子命令的控制命令类
 *
 * @author zkpursuit
 */
abstract public class MacroCommand extends Command {

    private Collection<Command> subCommands = null;

    public MacroCommand() {
        this.subCommands = new Stack<>();
        this.__init();
    }
    
    private void __init() {
        initializeMacroCommand();
    }

    /**
     * 必须在子类中实现此方法，并在方法体中调用addSubCommand方法添加子命令
     */
    abstract protected void initializeMacroCommand();

    final protected void addSubCommand(Command commandClassRef) {
        this.subCommands.add(commandClassRef);
    }

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
