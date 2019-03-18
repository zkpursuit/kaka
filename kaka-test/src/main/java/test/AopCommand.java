package test;

import com.kaka.aop.annotation.Intercept;
import com.kaka.notice.Command;
import com.kaka.notice.Message;
import com.kaka.notice.annotation.Handler;

/**
 *
 * @author zkpursuit
 */
@Handler(cmd = "AopCommand", type = String.class)
public class AopCommand extends Command {

    @Intercept(test.MyMethodInterceptor.class)
    @Override
    public void execute(Message msg) {
        System.out.println("AopCommand.execute");
//        int a = 100 / 0;
//        this.execute(100);
    }

//    public void execute(int v) {
//        System.out.println(v);
//    }

}
