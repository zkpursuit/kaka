package com.test.units;

import com.kaka.notice.Proxy;
import com.kaka.notice.annotation.Model;

/**
 * 
 * @author zkpursuit
 */
@Model
public class MyProxy extends Proxy {

    public void func() {
        System.out.println("调用了：" + MyProxy.class.getTypeName() + " -> func方法");
    }

}
