package kaka;

import com.kaka.Startup;
import static com.kaka.notice.Facade.facade;

import com.kaka.aop.Aop;
import com.kaka.aop.AopFactory;
import com.kaka.notice.Message;
import com.kaka.util.ReflectUtils;
import test.MyObject;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zkpursuit
 */
public class TestAop extends Startup {
    
    public static void main(String[] args) {
        TestAop test = new TestAop();
        test.scan("test");
        facade.sendMessage(new Message("AopCommand", "让MyCommand接收执行"));

        Aop aop = AopFactory.getAop(); //已扫描处理切面类MyAspect，故而此处直接通过aop创建实例
        MyObject obj = aop.createInstance(MyObject.class);
        obj.set(1, 2);
    }
    
}
