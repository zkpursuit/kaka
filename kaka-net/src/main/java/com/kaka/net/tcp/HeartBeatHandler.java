package com.kaka.net.tcp;

import com.kaka.net.Events;
import com.kaka.notice.Message;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

import static com.kaka.notice.Facade.facade;

/**
 * 心跳处理
 *
 * @author zhoukai
 */
public class HeartBeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (null != e.state()) switch (e.state()) {
                case READER_IDLE:
                    //读超时
                    facade.sendMessage(new Message(Events.CHANNEL_READER_IDLE, ctx));
                    break;
                case WRITER_IDLE:
                    //写超时
                    facade.sendMessage(new Message(Events.CHANNEL_WRITER_IDLE, ctx));
                    break;
                case ALL_IDLE:
                    facade.sendMessage(new Message(Events.CHANNEL_RW_IDLE, ctx));
                    break;
                default:
                    break;
            }
        }
    }

}
