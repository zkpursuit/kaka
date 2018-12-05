package test;

import com.kaka.notice.Command;
import com.kaka.notice.Message;
import com.kaka.notice.annotation.Handler;

/**
 *
 * @author zkpursuit
 */
@Handler(cmd = "1000", type = String.class)
@Handler(cmd = "2000", type = String.class)
public class MyCommand extends Command {

    @Override
    public void execute(Message msg) {
        System.out.println(MyCommand.class.getTypeName() + " -> execute " + msg.getWhat() + " 绑定的数据：" + msg.getBody());
        MyProxy proxy = this.getProxy(MyProxy.class);
        proxy.func();
        this.sendMessage(new Message("3000", "让MyMediator接收执行"));
    }

}
