package kaka;

import com.kaka.Startup;
import com.kaka.notice.AsynResult;
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
        //同步发送事件通知
        facade.sendMessage(new Message("1000", "让MyCommand接收执行"));
        //简单的异步发送事件通知
        facade.sendMessage(new Message("2000", "让MyMediator和MyCommand接收执行"), true);

        //future模式获取异步处理结果
        Message asynMsg = new Message("10000", "让AsynCommand接收执行");
        //由于事件通知为广播模式，故而必须为执行结果进行命名标识唯一性
        AsynResult result = (AsynResult) asynMsg.setResult("AsynMsg", new AsynResult());
        facade.sendMessage(asynMsg, true);
        if (result != null) {
            Object resultObject = result.get();
            System.out.println(resultObject);
        }
    }
}
