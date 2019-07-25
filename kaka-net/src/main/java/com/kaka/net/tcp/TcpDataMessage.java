package com.kaka.net.tcp;

import com.kaka.notice.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author zhoukai
 */
public class TcpDataMessage extends Message {
    
    protected ChannelHandlerContext ctx;

    public TcpDataMessage(Object what, Object data, ChannelHandlerContext ctx) {
        super(what, data);
        this.ctx = ctx;
    }
    
    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    
    @Override
    public void reset() {
        ctx = null;
        super.reset();
    }
    
}
