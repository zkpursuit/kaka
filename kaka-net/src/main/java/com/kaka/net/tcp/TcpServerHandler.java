package com.kaka.net.tcp;

import com.kaka.net.Events;
import com.kaka.notice.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.kaka.notice.Facade.facade;

/**
 *
 * @author zkpursuit
 */
public class TcpServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int opcode = msg.readShort();
        facade.sendMessage(new TcpDataMessage(opcode, msg, ctx));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        facade.sendMessage(new Message(Events.CHANNEL_ACTIVE, ctx));
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        facade.sendMessage(new Message(Events.CHANNEL_EXCEPTION, new Object[]{ctx, cause}));
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        facade.sendMessage(new Message(Events.CHANNEL_REMOVE, ctx));
        super.handlerRemoved(ctx);
    }

}
