package com.test.units;

import com.kaka.notice.Command;
import com.kaka.notice.Message;
import com.kaka.notice.annotation.Handler;

/**
 *
 * @author zkpursuit
 */
@Handler(cmd = "AopCommand", type = String.class)
public class AopCommand extends Command {

    @Override
    public void execute(Message msg) {
        System.out.println("-------------- AopCommand.execute -----------------");
//        int a = 100 / 0;
        this.execute(100, 200);
    }

    public void execute(int a, int b) {
    }

}
