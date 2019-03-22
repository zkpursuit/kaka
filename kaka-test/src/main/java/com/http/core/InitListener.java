package com.http.core;

import com.kaka.Startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitListener extends Startup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.scan("com.http.business");
    }

}
