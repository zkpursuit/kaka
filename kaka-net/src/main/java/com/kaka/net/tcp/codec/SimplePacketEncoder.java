package com.kaka.net.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

/**
 * 简单数据包编码器，数据包格式：包体字节长度 + 包体内容
 */
public class SimplePacketEncoder extends MessageToByteEncoder<Object> {

    private final static int LENGTH = 4;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object object, ByteBuf out) throws Exception {
        Channel channel = ctx.channel();
        if (channel == null) {
            return;
        }
        if (!channel.isActive()) {
            return;
        }
        if (!channel.isOpen()) {
            return;
        }
        if (!channel.isWritable()) {
            return;
        }
        if (!channel.isRegistered()) {
            return;
        }
        buildPacket(object, out);
    }

    /**
     * 构建一个发送的数据
     *
     * @param object 发送的数据
     * @param out 组装的数据包
     */
    protected void buildPacket(Object object, ByteBuf out) {
        if (object instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) object;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes, 0, bytes.length);
            buf.clear();
            int packetLen = bytes.length;
            buf.capacity(LENGTH + packetLen);
            buf.writeInt(packetLen);
            buf.writeBytes(bytes);
            bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes, 0, bytes.length);
            out.writeBytes(bytes);
            ReferenceCountUtil.release(buf);
        }
    }


}
