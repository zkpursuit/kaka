package test;

import com.kaka.notice.Command;
import com.kaka.notice.IResult;
import com.kaka.notice.Message;
import com.kaka.notice.annotation.Handler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zkpursuit
 */
@Handler(cmd = "30000", type = String.class)
public class FutureCommand extends Command {

    @Override
    public void execute(Message msg) {
        FutureTask<String> ft = new FutureTask<>(() -> {
            Thread.sleep(3000); //模拟耗时操作
            return ">>>>>>>>异步执行结果";
        });
        new Thread(ft).start();
        try {
            IResult result = msg.getResult("ResultMsg");
            if(result != null) {
                result.set(ft.get()); 
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(FutureCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
