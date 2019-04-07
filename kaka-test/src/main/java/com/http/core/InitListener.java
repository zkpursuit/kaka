package com.http.core;

import com.kaka.Startup;
import static com.kaka.notice.Facade.facade;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitListener extends Startup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //扫描自动注册Command、Proxy、Mediator
        this.scan("com.http.business");
        //手动注册Proxy
        JsonFilterGroup filterGroup = facade.registProxy(JsonFilterGroup.class);
        //自行实现添加其它过滤器
        //filterGroup.addFilter
    }

}
