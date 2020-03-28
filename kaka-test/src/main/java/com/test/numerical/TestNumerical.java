package com.test.numerical;

import com.kaka.Startup;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.numerical.TextNumericConfig;
import com.kaka.util.ResourceUtils;

import java.io.InputStream;

public class TestNumerical extends Startup {

    public static void main(String[] args) {
        Facade facade = FacadeFactory.getFacade();
        TestNumerical test = new TestNumerical();
        test.scan("com.test.numerical");
        String[] fileNames = new String[]{"conf_dailySign.txt", "conf_item.txt"};
        for (String fileName : fileNames) {
            try (InputStream is = ResourceUtils.getResourceAsStream(fileName, TestNumerical.class)) {
                TextNumericConfig config = facade.retrieveProxy(fileName);
                config.parse(is, "UTF-8", 1);
                System.out.println("配置文件解析完成：" + fileName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
