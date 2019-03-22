package com.test.units;

import com.kaka.notice.Mediator;
import com.kaka.notice.Message;
import com.kaka.notice.annotation.MultiHandler;

/**
 *
 * @author zkpursuit
 */
@MultiHandler
public class MyMediator extends Mediator {

    /**
     * 处理感兴趣的事件
     *
     * @param msg 事件通知
     */
    @Override
    public void handleMessage(Message msg) {
        Object what = msg.getWhat();
        String cmd = String.valueOf(what);
        switch (cmd) {
            case "2000":
                System.out.println(MyMediator.class.getTypeName() + " -> handleMessage " + msg.getWhat() + " 绑定的数据：" + msg.getBody());
                break;
            case "3000":
                System.out.println(MyMediator.class.getTypeName() + " -> handleMessage " + msg.getWhat() + " 绑定的数据：" + msg.getBody());
                break;
        }
    }

    /**
     * 申明感兴趣的事件
     *
     * @return 感兴趣的事件
     */
    @Override
    public Object[] listMessageInterests() {
        return new Object[]{"2000", "3000"};
    }

}
