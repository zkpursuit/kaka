package com.kaka.net;

/**
 *
 * @author zkpursuit
 */
public final class Events {
    /**
     * msg.getBody() == [ChannelHandlerContext, Throwable]
     */
    public static final String CHANNEL_EXCEPTION = "channel_execption";
    /**
     * msg.getBody() == ChannelHandlerContext
     */
    public static final String CHANNEL_ACTIVE = "channel_active";
    /**
     * msg.getBody() == ChannelHandlerContext
     */
    public static final String CHANNEL_REMOVE = "channel_remove";
    
    /**
     * msg.getBody() == ChannelHandlerContext
     */
    public static final String CHANNEL_READER_IDLE = "channel_reader_idle";
    /**
     * msg.getBody() == ChannelHandlerContext
     */
    public static final String CHANNEL_WRITER_IDLE = "channel_writer_idle";
    /**
     * msg.getBody() == ChannelHandlerContext
     */
    public static final String CHANNEL_RW_IDLE = "channel_rw_idle";
}
