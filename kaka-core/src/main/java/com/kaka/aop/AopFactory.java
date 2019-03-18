package com.kaka.aop;

public class AopFactory {

    private static AbstractAop aop;

    static {
        try {
            Class<? extends AbstractAop> aopClass = (Class<? extends AbstractAop>) Class.forName("com.kaka.aopwear.CglibAop");
            aop = (AbstractAop) com.kaka.util.ReflectUtils.newInstance(aopClass);
        } catch (ClassNotFoundException e) {
        }
    }

    public static AbstractAop getFactory() {
        return aop;
    }

}
