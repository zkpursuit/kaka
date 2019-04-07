package com.http.core;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.http.util.JsonUtils;
import com.kaka.util.Charsets;
import com.kaka.util.IOUtils;
import com.kaka.util.concurrent.RateLimiter;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import static com.kaka.notice.Facade.facade;

/**
 * HTTP通信接口
 */
public class JsonApiServlet extends HttpServlet {

    protected static final Logger logger = (Logger) LoggerFactory.getLogger(JsonApiServlet.class);

    //令牌桶限流
    private RateLimiter limiter = null;

    private JsonFilterGroup filterGroup;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        limiter = RateLimiter.create(300); //每秒300个请求
        filterGroup = facade.retrieveProxy(JsonFilterGroup.class);
    }

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    protected String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 处理请求中的数据通信协议，json格式
     *
     * @param request       http请求
     * @param requestString 请求json格式字符串，可为单个JsonObject或者包含多个JsonObject的JsonArray
     *                      <p>也可能为经过DES加密再经过Hax编码后的字符串，但解码后仍是json格式</p>
     * @param response      http响应
     */
    private void processJson(String requestString, HttpServletRequest request, HttpServletResponse response) {
        String requestJsonString = null;
        if (JsonUtils.isValidJson(requestString)) {
            requestJsonString = requestString;
        }
        HttpJsonRespWriter writer = new HttpJsonRespWriter();
        JsonNode jn = JsonUtils.toJsonNode(requestJsonString);
        if (jn != null) {
            String ip = getClientIpAddress(request);
            if (jn instanceof ObjectNode) {
                ObjectNode jsonObj = (ObjectNode) jn;
                jsonObj.put("client_ip", ip);
                processJson(jsonObj, writer);
            } else if (jn instanceof ArrayNode) {
                ArrayNode jsonArr = (ArrayNode) jn;
                int size = jsonArr.size();
                for (int i = 0; i < size; i++) {
                    ObjectNode jsonObj = (ObjectNode) jsonArr.get(i);
                    jsonObj.put("client_ip", ip);
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
    protected void send(HttpServletResponse resp, String content) {
        try (PrintWriter pw = resp.getWriter()) {
            pw.write(content);
            pw.flush();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (limiter.tryAcquire(1, 5000, TimeUnit.MILLISECONDS)) {
            req.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html;charset=UTF-8");
            String param = req.getQueryString();
            param = URLDecoder.decode(param, "UTF-8");
            processJson(param, req, resp);
        } else {
            ObjectNode jo = JsonUtils.createJsonObject();
            jo.put("info", "服务忙，请稍等……");
            this.send(resp, JsonUtils.toJsonString(jo));
            logger.info("服务忙，请稍等……");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (limiter.tryAcquire(1, 5000, TimeUnit.MILLISECONDS)) {
            req.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html;charset=UTF-8");
            ServletInputStream is = req.getInputStream();
            byte[] bytes = IOUtils.readBytes(is);
            String param = new String(bytes, Charsets.utf8);
            processJson(param, req, resp);
        } else {
            ObjectNode jo = JsonUtils.createJsonObject();
            jo.put("info", "服务忙，请稍等……");
            this.send(resp, JsonUtils.toJsonString(jo));
            logger.info("服务忙，请稍等……");
        }
    }
}
