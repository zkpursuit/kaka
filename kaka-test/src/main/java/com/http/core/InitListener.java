package com.http.core;

import com.kaka.Startup;
import com.kaka.notice.FacadeFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitListener extends Startup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //扫描自动注册Command、Proxy、Mediator
        this.scan("com.http.business");
        //手动注册Proxy
        JsonFilterGroup filterGroup = FacadeFactory.getFacade().registerProxy(JsonFilterGroup.class);
        //自行实现添加其它过滤器
        //filterGroup.addFilter
    }

}
