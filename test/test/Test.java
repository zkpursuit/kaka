package test;


import com.kaka.Startup;
import static com.kaka.notice.Facade.facade;
import com.kaka.notice.Message;


/**
 *
 * @author zkpursuit
 */
public class Test extends Startup {

    public static void main(String[] args) {
        Test test = new Test();
        test.scan("test");
        facade.sendMessage(new Message("1000", "让MyCommand接收执行"));
        facade.sendMessage(new Message("2000", "让MyMediator和MyCommand接收执行"));
    }

}
