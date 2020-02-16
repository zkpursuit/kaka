package com.test;

import com.kaka.Startup;
import com.kaka.aop.Aop;
import com.kaka.aop.AopFactory;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Message;
import com.test.units.MyObject1;
import com.test.units.MyObject2;

/**
 *
 * @author zkpursuit
 */
public class TestAop extends Startup {
    
    public static void main(String[] args) {

        Facade facade = FacadeFactory.getFacade();

        TestAop test = new TestAop();
        test.scan("com.test.units");
        facade.sendMessage(new Message("AopCommand", "让AopCommand接收执行"));

        Aop aop = AopFactory.getAop(); //已扫描处理切面类MyAspect，故而此处无需再次注册即可直接通过aop创建实例
        MyObject1 obj1 = aop.createInstance(MyObject1.class);
        obj1.set(1, 2);
        MyObject2 obj2 = aop.createInstance(MyObject2.class);
        obj2.say("我说啥了？");
    }
    
}
