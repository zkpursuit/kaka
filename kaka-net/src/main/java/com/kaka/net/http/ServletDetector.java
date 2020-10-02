package com.kaka.net.http;

import com.kaka.net.http.annotation.WebInitParam;
import com.kaka.net.http.annotation.WebServlet;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.detector.IDetector;
import com.kaka.util.ReflectUtils;
import com.kaka.util.StringUtils;

import java.util.Map;

/**
 * 基于{@link Servlet}的注册器
 *
 * @author zkpursuit
 */
public class ServletDetector implements IDetector {

    private final Facade httpFacade = FacadeFactory.getFacade("HTTP_FACADE");

    @Override
    public String name() {
        return "httpRoute";
    }

    /**
     * 注册HTTP URL路由，类似J2EE Servlet，如果有HTTP的需求，必须在子类中实现该方法
     *
     * @param cls 待注册的类
     * @return 注册后的{@link HttpServlet}
     */
    @Override
    public Object discern(Class<?> cls) {
        if (!Servlet.class.isAssignableFrom(cls)) {
            return null;
        }
        WebServlet ws = cls.getAnnotation(WebServlet.class);
        if (ws == null) {
            return null;
        }
        if (!StringUtils.isNotEmpty(ws.url())) {
            return null;
        }
        String url = ws.url();
        url = url.replace('\\', '/');
        int len = url.length();
        if (len > 1) {
            int lastIdx = url.length() - 1;
            char lastChar = url.charAt(lastIdx);
            if (lastChar == '/') {
                url = url.substring(0, lastIdx);
            }
            char firstChar = url.charAt(0);
            if (firstChar != '/') {
                url = "/" + url;
            }
        }
        String[] names;
        if (StringUtils.isNotEmpty(ws.name())) {
            names = new String[]{url, ws.name()};
        } else {
            names = new String[]{url};
        }
        Servlet servlet = httpFacade.registerProxy((Class<? extends Servlet>) cls, names);
        WebInitParam[] params = ws.initParams();
        if (params != null && params.length > 0) {
            Map<String, String> paramsMap = (Map<String, String>) ReflectUtils.getFieldValue(servlet, "params");
            if (paramsMap != null) {
                for (WebInitParam param : params) {
                    paramsMap.put(param.name(), param.value());
                }
            }
        }
        return servlet;
    }

}
