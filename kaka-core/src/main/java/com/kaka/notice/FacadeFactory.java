package com.kaka.notice;

import com.kaka.util.ReflectUtils;

import java.util.HashMap;
import java.util.Map;

public class FacadeFactory {

    private static Class<? extends Facade> facadeClass = Facade.class;
    private static final String DEFAULT = "default";
    static final Map<String, Facade> instanceMap = new HashMap<>();

    /**
     * 必须先调用此方法进行配置 </br>
     * 一般在应用程序启动时调用，且仅调用一次
     *
     * @param facadeClass
     * @param <T>
     * @return
     */
    public synchronized static <T extends Facade> T config(Class<? extends Facade> facadeClass) {
        FacadeFactory.facadeClass = facadeClass;
        return getFacade();
    }

    public synchronized static <T extends Facade> T getFacade(String name) {
        Facade inst;
        if (instanceMap.get(name) == null) {
            try {
                inst = (Facade) ReflectUtils.newInstance(facadeClass);
                inst.__name = name;
                instanceMap.put(name, inst);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            inst = instanceMap.get(name);
        }
        return (T) inst;
    }

    public synchronized static <T extends Facade> T getFacade() {
        return getFacade(DEFAULT);
    }

    public synchronized static boolean hasFacade(String name) {
        return instanceMap.containsKey(name);
    }

    public synchronized static void removeFacade(String name) {
        Facade inst = instanceMap.remove(name);
        if (inst != null) {
            inst.dispose();
        }
    }

}
