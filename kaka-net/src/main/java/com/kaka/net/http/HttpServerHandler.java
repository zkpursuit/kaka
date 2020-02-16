package com.kaka.net.http;

import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.util.Collections;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HTTP请求处理
 *
 * @author zkpursuit
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    private final String root;
    private final Facade httpFacade = FacadeFactory.getFacade("HTTP_FACADE");

    public HttpServerHandler(String root) {
        this.root = root;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        String info = status + "\r\n" + cause + "\r\n";
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(info, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Set<Cookie> cookies;
        String cookie = request.headers().get(COOKIE);
        if (cookie == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = ServerCookieDecoder.STRICT.decode(cookie);
        }

        if (HttpUtil.is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }
        String requestUri = request.uri();
        //requestUri = QueryStringDecoder.decodeComponent(requestUri, Charsets.utf8);
        requestUri = requestUri.replaceAll("[/]+", "/");
        if (root != null && !root.equals("")) {
            int idx = -1;
            for (int i = 1; i < requestUri.length(); i++) {
                char c = requestUri.charAt(i);
                if (c == '/') {
                    idx = i;
                    break;
                }
            }
            if (idx > 0) {
                String rootUri = requestUri.substring(1, idx);
                if (rootUri.equals(root)) {
                    requestUri = requestUri.substring(idx);
                } else {
                    sendError(ctx, HttpResponseStatus.BAD_GATEWAY);
                    return;
                }
            } else if (!requestUri.equals(root)) {
                sendError(ctx, HttpResponseStatus.BAD_GATEWAY);
                return;
            }
        }

        int idx = requestUri.indexOf("?");
        if (idx > 0) {
            requestUri = requestUri.substring(0, idx);
        }
        int len = requestUri.length();
        if (len > 1) {
            int lastIdx = requestUri.length() - 1;
            char lastChar = requestUri.charAt(lastIdx);
            if (lastChar == '/') {
                requestUri = requestUri.substring(0, lastIdx);
            }
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        HttpRoute servlet = httpFacade.retrieveProxy(requestUri);
        if (servlet != null) {
            HttpMethod httpMethod = request.method();
            if (httpMethod == HttpMethod.GET) {
                servlet.doGet(request, response);
            } else if (httpMethod == HttpMethod.POST) {
                servlet.doPost(request, response);
            } else if (httpMethod == HttpMethod.PUT) {
                servlet.doPut(request, response);
            } else if (httpMethod == HttpMethod.DELETE) {
                servlet.doDelete(request, response);
            } else if (httpMethod == HttpMethod.HEAD) {
                servlet.doHead(request, response);
            } else if (httpMethod == HttpMethod.OPTIONS) {
                servlet.doOptions(request, response);
            } else if (httpMethod == HttpMethod.PATCH) {
                servlet.doPatch(request, response);
            } else if (httpMethod == HttpMethod.TRACE) {
                servlet.doTrace(request, response);
            } else {
                servlet.doOther(request, response);
            }
        } else {
            sendError(ctx, HttpResponseStatus.BAD_GATEWAY);
            return;
        }
        if (!cookies.isEmpty()) {
            cookies.stream().forEach((cook) -> {
                response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cook));
            });
        }
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (!HttpUtil.isKeepAlive(request)) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }

//    protected void doGet(HttpRequest request, HttpResponse response) {
//        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri(), Charset.utf8);
//        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
//        uriAttributes.forEach((String key, List<String> values) -> {
//            //System.out.print("paramName：" + key + "  ");
//            values.forEach((value) -> {
//                //System.out.print(value + ", ");
//            });
//            //System.out.print("\n");
//        });
//    }
//
//    protected void doPost(HttpRequest request, HttpResponse response) {
//        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE, Charset.utf8), request, Charset.utf8);
//        List<InterfaceHttpData> list = decoder.getBodyHttpDatas();
//        if (!list.isEmpty()) {
//            //处理键值对
//            for (InterfaceHttpData data : list) {
////                    System.out.println(data);
////                    MixedAttribute ma = (MixedAttribute) data;
//                if (data.getHttpDataType() == HttpDataType.Attribute) {
//                    Attribute attr = (Attribute) data;
//                    //System.out.println("----------***** >>> " + attr.getName() + " : " + attr.getString(Charset.utf8));
////                        System.out.println("-----" + attr.toString());
//                } else if (data.getHttpDataType() == HttpDataType.FileUpload) {
//                    //客户端上传的文件
//                    //MixedFileUpload mfu = (MixedFileUpload) data;
////                        mfu.setCharset(Charset.utf8);
//                    //String name = new String(data.getName().getBytes(), "utf-8");
//                    //System.out.println(data.getName() + "   >>> " + data);
//                }
//                data.release();
//            }
//        } else {
//            //客户端直接发送字节数据
//            ByteBuf content = request.content();
//            byte[] bytes = new byte[content.readableBytes()];
//            content.readBytes(bytes);
//            String str = new String(bytes, Charset.utf8);
//            System.out.println("---------------" + str);
//        }
//        //decoder.destroy();
//    }
}
