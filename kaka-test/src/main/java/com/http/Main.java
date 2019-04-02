package com.http;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.kaka.util.ResourceUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * 使用Jetty测试事件驱动 <br>
 * 浏览器输入 http://127.0.0.1:8080/index.html
 */
public class Main {

    /**
     * 手动初始化logback日志组件
     */
    static void initLogback() {
        Logger logger = LoggerFactory.getLogger(Main.class);
        InputStream is = ResourceUtils.getResourceAsStream("_logback.xml", Main.class);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        loggerContext.reset();
        try {
            joranConfigurator.doConfigure(is);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void main(String[] args) {
        initLogback();
        Main main = new Main();
        main.startUpHttpServer();
    }

    void startUpHttpServer() {
        String classRootPath = ResourceUtils.getClassLoaderPath(Main.class);
        String webPath;
        if(classRootPath.endsWith("classes")) {
            File file = new File(classRootPath);
            file = file.getParentFile().getParentFile();
            webPath = file.getAbsolutePath().replaceAll("\\\\", "/");
        } else {
            webPath = classRootPath.replaceAll("\\\\", "/");
        }
        webPath = webPath + "/web";
        final Server server = new Server(8080);
        WebAppContext webapp_context = new WebAppContext();
        webapp_context.setDescriptor(webPath + "/WEB-INF/web.xml");
        webapp_context.setResourceBase(webPath);
        webapp_context.setContextPath("/");
        webapp_context.setParentLoaderPriority(true);
        webapp_context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setStopAtShutdown(true);
        server.setHandler(webapp_context);
        new Thread(() -> {
            try {
                server.start();
                server.join();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}
