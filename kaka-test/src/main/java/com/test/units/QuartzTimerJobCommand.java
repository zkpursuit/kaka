package com.test.units;

import com.kaka.notice.Command;
import com.kaka.notice.Message;

public class QuartzTimerJobCommand extends Command {
    @Override
    public void execute(Message msg) {
        System.out.println(MyCommand.class.getTypeName() + " -> execute " + msg.getWhat() + " ==>> " + msg.getBody());
    }
}
