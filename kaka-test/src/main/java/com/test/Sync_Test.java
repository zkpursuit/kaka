package com.test;

import com.kaka.Startup;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Message;

/**
 * 同步使用范例
 *
 * @author zkpursuit
 */
public class Sync_Test extends Startup {

    public static void main(String[] args) {
        Facade facade = FacadeFactory.getFacade();
        Sync_Test test = new Sync_Test();
        test.scan("com.test.units");
        facade.sendMessage(new Message("1000", "让MyCommand接收执行"));
        facade.sendMessage(new Message("2000", "让MyMediator和MyCommand接收执行"));
    }

}
