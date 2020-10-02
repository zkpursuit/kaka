package com.kaka.net.tcp;

import com.kaka.net.TcpStateCode;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zkpursuit
 */
abstract public class TcpServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final Facade facade = FacadeFactory.getFacade();

    /**
     * 处理数据包
     *
     * @param ctx
     * @param msg 一个数据包
     */
    abstract protected void doDataPacket(ChannelHandlerContext ctx, ByteBuf msg);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        doDataPacket(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        facade.sendMessage(new Message(TcpStateCode.CHANNEL_ACTIVE, ctx));
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        facade.sendMessage(new Message(TcpStateCode.CHANNEL_EXCEPTION, new Object[]{ctx, cause}));
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        facade.sendMessage(new Message(TcpStateCode.CHANNEL_REMOVE, ctx));
        super.handlerRemoved(ctx);
    }

}
