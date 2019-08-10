package com.http.core;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.http.util.JsonUtils;
import com.kaka.net.http.HttpRoute;
import com.kaka.net.http.annotation.WebInitParam;
import com.kaka.net.http.annotation.WebRoute;
import com.kaka.util.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.kaka.notice.Facade.facade;

@WebRoute(url = "/myservlet", initParams = {@WebInitParam(name = "a", value = "100")})
public class JsonApiMyServlet extends HttpRoute {

    protected static final Logger logger = (Logger) LoggerFactory.getLogger(JsonApiMyServlet.class);

    private JsonFilterGroup filterGroup;

    public JsonApiMyServlet() {
        filterGroup = facade.retrieveProxy(JsonFilterGroup.class);
    }

    /**
     * 处理请求中的数据通信协议，json格式
     *
     * @param request       http请求
     * @param requestString 请求json格式字符串，可为单个JsonObject或者包含多个JsonObject的JsonArray
     *                      <p>也可能为经过DES加密再经过Hax编码后的字符串，但解码后仍是json格式</p>
     * @param response      http响应
     */
    private void processJson(String requestString, HttpRequest request, HttpResponse response) {
        String requestJsonString = null;
        if (JsonUtils.isValidJson(requestString)) {
            requestJsonString = requestString;
        }
        HttpJsonRespWriter writer = new HttpJsonRespWriter();
        JsonNode jn = JsonUtils.toJsonNode(requestJsonString);
        if (jn != null) {
            if (jn instanceof ObjectNode) {
                ObjectNode jsonObj = (ObjectNode) jn;
                processJson(jsonObj, writer);
            } else if (jn instanceof ArrayNode) {
                ArrayNode jsonArr = (ArrayNode) jn;
                int size = jsonArr.size();
                for (int i = 0; i < size; i++) {
                    ObjectNode jsonObj = (ObjectNode) jsonArr.get(i);
                    processJson(jsonObj, writer);
                }
            }
            if (writer.isEmpty()) {
                ObjectNode jo = JsonUtils.createJsonObject();
                jo.put("info", "module_not_open");
                writer.writeObject(jo);
            }
        } else {
            ObjectNode jo = JsonUtils.createJsonObject();
            jo.put("error", "请求数据非标准json格式");
            writer.writeObject(jo);
        }
        this.send(response, writer.toString());
    }

    /**
     * 处理单个JsonObject数据协议</br>
     * 单个处理当中发生异常不会影响其它处理结果</br>
     *
     * @param jsonObj json协议数据
     * @param out     整合处理结果
     */
    private void processJson(ObjectNode jsonObj, HttpJsonRespWriter out) {
        String cmd = jsonObj.get("cmd").asText();
        HttpJsonRespWriter writer = new HttpJsonRespWriter();
        try {
            processJson(cmd, jsonObj, writer);
        } catch (Throwable ex) {
            ObjectNode jo = JsonUtils.createJsonObject();
            jo.put("info", "error");
            writer.write(cmd, jo);
            logger.error(JsonUtils.toJsonString(jsonObj));
            logger.error("数据处理错误！", ex);
        }
        if (writer.isEmpty()) {
            ObjectNode jo = JsonUtils.createJsonObject();
            jo.put("info", "module_not_open");
            writer.write(cmd, jo);
        }
        out.writeObject(writer);
    }

    protected void processJson(String cmd, ObjectNode jsonObj, HttpJsonRespWriter writer) throws Throwable {
        this.filterGroup.doFilter(cmd, jsonObj, writer);
    }

    /**
     * 发送响应数据到客户端
     *
     * @param resp
     * @param content
     */
    protected void send(HttpResponse resp, String content) {
        FullHttpResponse fullResponse = (FullHttpResponse) resp;
        ByteBuf buf = fullResponse.content();
        buf.writeBytes(content.getBytes(Charsets.utf8));
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri(), Charsets.utf8);
        String queryStr = decoderQuery.rawQuery();
        try {
            queryStr = URLDecoder.decode(queryStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String param = queryStr;
        processJson(param, request, response);


//        System.out.println(queryStr);
//        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
//        uriAttributes.forEach((String key, List<String> values) -> {
//            System.out.print("paramName：" + key + "  ");
//            values.forEach((value) -> {
//                System.out.print(value + ", ");
//            });
//            System.out.print("\n");
//        });
//        FullHttpResponse fullResponse = (FullHttpResponse) response;
//        ByteBuf buf = fullResponse.content();
//        buf.writeBytes("我爱我家".getBytes(Charsets.utf8));
//        buf.writeByte('\r');
//        buf.writeByte('\n');
//        System.out.println(getInitParameter("a"));
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        FullHttpRequest fullRequest = (FullHttpRequest) request;
        ByteBuf content = fullRequest.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        String param = new String(bytes, Charsets.utf8);
        processJson(param, request, response);
    }

}