package com.test;

import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Message;
import com.test.quartz.QuartzFacade;
import com.test.units.QuartzTimerJobCommand;

import java.util.Date;

public class TestQuartz {

    public static void main(String[] args) {
        QuartzFacade facade = FacadeFactory.config(QuartzFacade.class);
        facade.registerCommand("test_11", QuartzTimerJobCommand.class);
        facade.registerCommand("test_12", QuartzTimerJobCommand.class);
        //以上可用其它范例Startup的scan类包扫描方式进行Command自动注册，此处为手动注册
        facade.sendMessage(new Message("test_11", "绑定的数据1"), "test1", "*/2 * * * * ?"); //每隔2秒执行一次
        facade.sendMessage(new Message("test_12", "绑定的数据2"), "test1", new Date(System.currentTimeMillis() + 3000)); //当前时间间隔3秒后执行一次
    }

}
