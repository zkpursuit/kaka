package com.kaka.net.tcp;

import ch.qos.logback.classic.Logger;
import com.kaka.notice.Command;
import com.kaka.notice.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;

/**
 * 字节数据包（自定义协议处理器）
 *
 * @author zhoukai
 */
abstract public class ByteDataHandler extends Command {

    private Logger logger;

    @Override
    public void execute(Message msg) {
        try {
            if (msg instanceof TcpDataMessage) {
                TcpDataMessage nm = (TcpDataMessage) msg;
                execute(nm.getCtx(), (ByteBuf) msg.getBody());
            }
        } catch (Exception ex) {
            getLogger().error(ex.getLocalizedMessage(), ex);
        }
    }

    protected final Logger getLogger() {
        if (logger == null) {
            logger = (Logger) LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    abstract public void execute(ChannelHandlerContext ctx, ByteBuf buf);

}
