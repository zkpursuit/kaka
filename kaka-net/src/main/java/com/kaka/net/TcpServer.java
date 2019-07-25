package com.kaka.net;

import ch.qos.logback.classic.Logger;
import com.kaka.net.tcp.TcpServerHandler;
import com.kaka.net.tcp.codec.SimplePacketDecoder;
import com.kaka.net.tcp.codec.SimplePacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * socket服务器，基于Netty4
 *
 * @author zhoukai
 */
public class TcpServer {

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected EventLoopGroup businessThreadGroup;

    private final Logger logger = (Logger) LoggerFactory.getLogger(TcpServer.class);

    /**
     * 启动服务
     *
     * @param address                绑定的地址
     * @param ioThreadPoolSize       IO线程池大小，处理read/write
     * @param businessThreadPoolSize 业务处理线程池中默认初始化线程
     * @param readerIdleTimeSeconds  读超时时间，0表示不可用
     * @param writerIdleTimeSeconds  写超时时间，0表示不可用
     * @param allIdleTimeSeconds     读写超时时间，0表示不可用
     */
    public void start(InetSocketAddress address, int ioThreadPoolSize, int businessThreadPoolSize, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        try {
            //用于接收所有连接到服务器端的客户端连接
            //要在EventLoopGroup创建多少个线程，映射多少个Channel可以在EventLoopGroup的构造方法中进行配置
            //处理socket握手连接
            bossGroup = new NioEventLoopGroup(2);
            //当有新的连接进来时将会被注册到worker中，理想线程数为CPU核数的2倍,此线程组为处理IO操作
            workerGroup = new NioEventLoopGroup(ioThreadPoolSize);
            //IO处理结束后的业务处理线程组
            //如果业务不复杂可以不需要下面的业务处理线程组
            if (businessThreadPoolSize > 0) {
                businessThreadGroup = new NioEventLoopGroup(businessThreadPoolSize);
            }
            //用于设置服务器的辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //bootstrap.option(ChannelOption.SO_TIMEOUT, this);
            //bootstrap.option(ChannelOption.s, this);
            logger.info("启动Socket服务，监听端口" + address.getPort());
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(buildChannelInitializer(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
            ChannelFuture f = bootstrap.bind(address).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            if (businessThreadGroup != null) {
                businessThreadGroup.shutdownGracefully();
            }
        }
    }

    /**
     * 启动服务，不启用心跳机制
     *
     * @param address                绑定的地址
     * @param ioThreadPoolSize       IO线程池大小，处理read/write
     * @param businessThreadPoolSize 业务处理线程池中默认初始化线程
     */
    public void start(InetSocketAddress address, int ioThreadPoolSize, int businessThreadPoolSize) {
        this.start(address, ioThreadPoolSize, businessThreadPoolSize, 0, 0, 0);
    }

    /**
     * 初始化编解码器
     *
     * @param pipeline
     */
    protected void initCodec(ChannelPipeline pipeline) {
        pipeline.addLast(new SimplePacketDecoder());
        pipeline.addLast(new SimplePacketEncoder());
    }

    /**
     * 构建socket信道<br>
     * readerIdleTimeSeconds、writerIdleTimeSeconds、allIdleTimeSeconds三个参数同时为负数时将不做心跳超时处理
     *
     * @param readerIdleTimeSeconds 指定时间段内未执行读操作，将触发读超时事件
     * @param writerIdleTimeSeconds 指定时间段内未执行写操作，将触发写超时事件
     * @param allIdleTimeSeconds    指定时间段内，既未执行读操作，也未执行写操作，将触发读写超时事件
     * @return socket信道初始化器
     */
    ChannelInitializer buildChannelInitializer(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        ChannelInitializer ci = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (readerIdleTimeSeconds > 0 || writerIdleTimeSeconds > 0 && allIdleTimeSeconds > 0) {
                    int readerIdleTime = readerIdleTimeSeconds;
                    if (readerIdleTimeSeconds <= 0) {
                        readerIdleTime = 0;
                    }
                    int writerIdleTime = writerIdleTimeSeconds;
                    if (writerIdleTimeSeconds <= 0) {
                        writerIdleTime = 0;
                    }
                    int allIdleTime = allIdleTimeSeconds;
                    if (allIdleTimeSeconds <= 0) {
                        allIdleTime = 0;
                    }
                    ch.pipeline().addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime));
                }
                initCodec(ch.pipeline());
                if (businessThreadGroup == null) {
                    ch.pipeline().addLast(buildServerHandler());
                } else {
                    ch.pipeline().addLast(businessThreadGroup, "business", buildServerHandler());
                }
            }
        };
        return ci;
    }

    protected TcpServerHandler buildServerHandler() {
        return new TcpServerHandler();
    }

    public void destroy() {
        if (bossGroup != null) {
            if (bossGroup.isShutdown() == false && bossGroup.isShuttingDown() == false) {
                bossGroup.shutdownGracefully();
            }
        }
        if (workerGroup != null) {
            if (workerGroup.isShutdown() == false && workerGroup.isShuttingDown() == false) {
                workerGroup.shutdownGracefully();
            }
        }
        if (businessThreadGroup != null) {
            if (businessThreadGroup.isShutdown() == false && businessThreadGroup.isShuttingDown() == false) {
                businessThreadGroup.shutdownGracefully();
            }
        }
    }

}
