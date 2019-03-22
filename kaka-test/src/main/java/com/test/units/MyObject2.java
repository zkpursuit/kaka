package com.test.units;

import com.kaka.aop.annotation.Intercept;

public class MyObject2 {

    @Intercept(MyMethodInterceptor.class)
    public void say(String content) {
        System.out.println(this.getClass().getTypeName() + " 说：" + content);
    }

}
