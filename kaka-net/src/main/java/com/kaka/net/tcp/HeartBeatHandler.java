package com.kaka.net.tcp;

import com.kaka.net.TcpStateCode;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Message;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳处理
 *
 * @author zhoukai
 */
public class HeartBeatHandler extends ChannelDuplexHandler {

    private final Facade facade = FacadeFactory.getFacade();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (null != e.state()) switch (e.state()) {
                case READER_IDLE:
                    //读超时
                    facade.sendMessage(new Message(TcpStateCode.CHANNEL_READER_IDLE, ctx));
                    break;
                case WRITER_IDLE:
                    //写超时
                    facade.sendMessage(new Message(TcpStateCode.CHANNEL_WRITER_IDLE, ctx));
                    break;
                case ALL_IDLE:
                    facade.sendMessage(new Message(TcpStateCode.CHANNEL_RW_IDLE, ctx));
                    break;
                default:
                    break;
            }
        }
    }

}
