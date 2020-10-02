package com.kaka.net;

import ch.qos.logback.classic.Logger;
import com.kaka.net.http.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;

/**
 * HTTP服务
 *
 * @author zkpursuit
 */
public class HttpServer {

    private final Logger logger = (Logger) LoggerFactory.getLogger(HttpServer.class);

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected EventLoopGroup businessThreadGroup;

    protected String root;

    protected SslContext sslCtx;

    /**
     * 构造方法
     *
     * @param root 项目根路径
     * @param SSL  是否启用https访问
     */
    public HttpServer(String root, boolean SSL) {
        if (root != null) {
            this.root = root.replace("/", "");
        } else {
            this.root = "";
        }
        if (SSL) {
            try {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (CertificateException | SSLException ex) {
                logger.error(ex.getLocalizedMessage(), ex);
            }
        } else {
            sslCtx = null;
        }
    }

    public HttpServer(String root) {
        this(root, false);
    }

    public HttpServer() {
        this("/", false);
    }

    /**
     * 启动服务
     *
     * @param port                   绑定的端口
     * @param workerThreadPoolSize   http协议解码线程组线程个数
     * @param businessThreadPoolSize 业务处理线程池线程个数，并发量大需调整此值
     */
    public void start(int port, int workerThreadPoolSize, int businessThreadPoolSize) {
        bossGroup = new NioEventLoopGroup();
        if (workerThreadPoolSize > 0) {
            workerGroup = new NioEventLoopGroup(workerThreadPoolSize);
        } else {
            workerGroup = new NioEventLoopGroup();
        }
        if (businessThreadPoolSize > 0) {
            businessThreadGroup = new NioEventLoopGroup(businessThreadPoolSize, Executors.newFixedThreadPool(businessThreadPoolSize));
        }
        final EventLoopGroup _btg = businessThreadGroup;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            /* 此处超时时间不是客户端TCP连接的超时时间，而是服务器处理的时间，如果超时，那么就会触发handler里面的exceptionCaught */
                            /* 当http connection设置为keep-alive时，后续无通信时将触发超时异常，属正常 */
                            ch.pipeline().addLast(new ReadTimeoutHandler(10)); //服务器端设置超时时间,单位：秒
                            ch.pipeline().addLast(new WriteTimeoutHandler(10)); //服务器端设置超时时间,单位：秒
                            if (sslCtx != null) {
                                ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(104857600));
                            /**
                             * 压缩 Compresses an HttpMessage and an HttpContent
                             * in gzip or deflate encoding while respecting the
                             * "Accept-Encoding" header. If there is no matching
                             * encoding, no compression is done.
                             */
                            ch.pipeline().addLast(new HttpContentCompressor());
                            if (_btg != null) {
                                ch.pipeline().addLast(_btg, "business", new HttpServerHandler(root));
                            } else {
                                ch.pipeline().addLast(new HttpServerHandler(root));
                            }
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            StringBuilder sb = new StringBuilder("  访问路径 ==>> ");
            if (sslCtx != null) {
                sb.append("https://");
            } else {
                sb.append("http://");
            }
            sb.append("127.0.0.1").append(":");
            sb.append(port);
            sb.append("/").append(root);
            logger.info(sb.toString());
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            businessThreadGroup.shutdownGracefully();
        }
    }

    public void stop() {
        if (bossGroup != null) {
            if (!bossGroup.isShutdown() && !bossGroup.isShuttingDown()) {
                bossGroup.shutdownGracefully();
            }
        }
        if (workerGroup != null) {
            if (!workerGroup.isShutdown() && !workerGroup.isShuttingDown()) {
                workerGroup.shutdownGracefully();
            }
        }
        if (businessThreadGroup != null) {
            if (!businessThreadGroup.isShutdown() && !businessThreadGroup.isShuttingDown()) {
                businessThreadGroup.shutdownGracefully();
            }
        }
    }

}
