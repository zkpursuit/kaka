package kaka;

import com.kaka.Startup;
import static com.kaka.notice.Facade.facade;
import com.kaka.notice.Message;
import java.util.concurrent.Executors;

/**
 * 异步使用范例
 *
 * @author zkpursuit
 */
public class Asyn_Test extends Startup {

    public static void main(String[] args) {
        Asyn_Test test = new Asyn_Test();
        test.scan("kaka.test");
        facade.initThreadPool(Executors.newFixedThreadPool(2));
        facade.sendMessage(new Message("1000", "让MyCommand接收执行")); //同步发送事件通知
        facade.sendMessage(new Message("2000", "让MyMediator和MyCommand接收执行"), true); //异步发送事件通知
    }
}
