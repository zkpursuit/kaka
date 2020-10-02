package com.kaka.net.http;

import com.kaka.notice.Proxy;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 类似Servlet
 *
 * @author zkpursuit
 */
public abstract class Servlet extends Proxy {

    final Map<String, String> params = new HashMap<>();

    public final String getInitParameter(String name) {
        return params.get(name);
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doPut(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doDelete(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doOptions(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doHead(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doPatch(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doTrace(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doOther(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

}
