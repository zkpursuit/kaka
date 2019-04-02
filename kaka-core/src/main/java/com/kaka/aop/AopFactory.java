package com.kaka.aop;

/**
 * Aop实例化工厂
 */
public class AopFactory {

    private static Aop aop;

    static {
        try {
            Class<? extends Aop> aopClass = (Class<? extends Aop>) Class.forName("com.kaka.aopwear.CglibAop");
            aop = (Aop) com.kaka.util.ReflectUtils.newInstance(aopClass);
        } catch (ClassNotFoundException e) {
        }
    }

    public static Aop getAop() {
        return aop;
    }

    private AopFactory() {
    }

}
