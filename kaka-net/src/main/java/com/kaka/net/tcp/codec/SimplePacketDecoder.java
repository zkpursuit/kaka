package com.kaka.net.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 简单数据包解码器，数据包格式：包体字节长度 + 包体内容
 */
public class SimplePacketDecoder extends ByteToMessageDecoder {

    private final static int LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        while (buf.readableBytes() > LENGTH) {
            buf.markReaderIndex();
            int dataLen = buf.readInt();
            if (dataLen <= 0) {
                continue;
            }
            if (dataLen > buf.readableBytes()) {
                //长度不足，等待下次数据接收
                buf.resetReaderIndex();
                break;
            }
            byte[] bytes = new byte[dataLen];
            buf.readBytes(bytes, 0, dataLen);
            buildPacket(ctx.alloc(), bytes, out);
        }
    }

    /**
     * 构建一个数据包
     *
     * @param alloc ByteBuf生成器
     * @param bytes 数据包内容
     * @param out 下级数据链
     */
    protected void buildPacket(ByteBufAllocator alloc, byte[] bytes, List<Object> out) {
        //去掉最后一个结束校验字节，可对bytes解密（如果有被加密）
        ByteBuf _buf = Unpooled.buffer(bytes.length);
        _buf.writeBytes(bytes);
        out.add(_buf);
    }
}
