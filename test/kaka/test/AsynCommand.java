package kaka.test;

import com.kaka.notice.AsynResult;
import com.kaka.notice.Command;
import com.kaka.notice.Message;
import com.kaka.notice.annotation.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zkpursuit
 */
@Handler(cmd = "10000", type = String.class)
public class AsynCommand extends Command {

    @Override
    public void execute(Message msg) {
        try {
            //模拟耗时操作
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AsynCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        AsynResult result = (AsynResult) msg.getResult("AsynMsg");
        if (result != null) {
            result.set("的金发的设计费的撒娇附带数据阿范德萨");
        }
    }

}
