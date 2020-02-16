package com.kaka_http;

import com.http.core.JsonFilterGroup;
import com.kaka.Startup;
import com.kaka.net.HttpServer;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;

public class TestHttpServer extends Startup {

    public static void main(String[] args) {
        Facade facade = FacadeFactory.getFacade();

        facade.registerProxy(JsonFilterGroup.class);

        TestHttpServer test = new TestHttpServer();
        test.scan("com.http.core", "com.http.business");

        HttpServer server = new HttpServer("myweb");
        server.start(8080, 10, 0);
    }

}
