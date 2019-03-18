package kaka;

import com.kaka.Startup;
import static com.kaka.notice.Facade.facade;
import com.kaka.notice.Message;

/**
 *
 * @author zkpursuit
 */
public class TestAop extends Startup {
    
    public static void main(String[] args) {
        TestAop test = new TestAop();
        test.scan("test");
        facade.sendMessage(new Message("AopCommand", "让MyCommand接收执行"));
    }
    
}
