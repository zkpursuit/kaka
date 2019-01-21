package kaka.test;

import com.kaka.notice.AsynResult;
import com.kaka.notice.Command;
import com.kaka.notice.IResult;
import com.kaka.notice.Message;
import com.kaka.notice.SyncResult;
import com.kaka.notice.annotation.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zkpursuit
 */
@Handler(cmd = "10000", type = String.class)
@Handler(cmd = "20000", type = String.class)
public class ResultCommand extends Command {

    @Override
    public void execute(Message msg) {
        try {
            //模拟耗时操作
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ResultCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        IResult result = msg.getResult("ResultMsg");
        if (result != null) {
            //必须设置处理结果
            if (result instanceof AsynResult) {
                result.set(">>>>>>>>异步执行结果");
            } else if (result instanceof SyncResult) {
                result.set(">>>>>>>>同步执行结果");
            }
        }
    }

}
