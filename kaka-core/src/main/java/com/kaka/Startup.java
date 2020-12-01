package com.kaka;

import com.kaka.aop.Aop;
import com.kaka.aop.AopFactory;
import com.kaka.aop.annotation.Aspect;
import com.kaka.notice.detector.CommandDetector;
import com.kaka.notice.detector.IDetector;
import com.kaka.notice.detector.MediatorDetector;
import com.kaka.notice.detector.ProxyDetector;
import com.kaka.numerical.NumericDetector;
import com.kaka.util.ClassScaner;

import java.util.*;

/**
 * 启动器，其中包含类扫描及事件通知模型的注册
 *
 * @author zhoukai
 */
public abstract class Startup {

    final Map<String, IDetector> detectorMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 构造方法
     *
     * @param registers 事件通知模型注册器
     */
    public Startup(IDetector... registers) {
        addDetector(new CommandDetector());
        addDetector(new MediatorDetector());
        addDetector(new ProxyDetector());
        addDetector(new NumericDetector());

        IDetector httpServletDetector = null;
        try {
            Class<? extends IDetector> httpServletDetectorClass = (Class<? extends IDetector>) Class.forName("com.kaka.net.http.HttpServletDetector");
            httpServletDetector = (IDetector) com.kaka.util.ReflectUtils.newInstance(httpServletDetectorClass);
        } catch (ClassNotFoundException e) {
        }
        if (httpServletDetector != null) {
            addDetector(httpServletDetector);
        }

        for (IDetector detector : registers) {
            addDetector(detector);
        }
    }

    /**
     * 添加事件通知模型注册器
     *
     * @param detector 事件通知模型注册器
     */
    final protected void addDetector(IDetector detector) {
        String name = detector.name();
        if (name == null) {
            name = detector.getClass().getTypeName();
        }
        detectorMap.put(name, detector);
    }

    /**
     * 初始化
     *
     * @param loader   从此类加载器中扫描加载类
     * @param packages .分割的包名
     * @return 扫描到的类集合
     */
    final protected Set<Class<?>> scan(ClassLoader loader, String... packages) {
        Set<Integer> delIdxs = new HashSet<>();
        //过滤子包和相同的包
        for (int i = 0; i < packages.length; i++) {
            String selectPackage = packages[i];
            for (int j = i + 1; j < packages.length; j++) {
                String currPackage = packages[j];
                if (selectPackage.length() < currPackage.length()) {
                    if (currPackage.contains(selectPackage)) {
                        delIdxs.add(j);
                    }
                } else if (selectPackage.length() > currPackage.length()) {
                    if (selectPackage.contains(currPackage)) {
                        delIdxs.add(i);
                    }
                } else if (selectPackage.equals(currPackage)) {
                    delIdxs.add(j);
                }
            }
        }
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        Set<Class<?>> classes = new HashSet<>();
        final Aop aop = AopFactory.getAop();
        for (int i = 0; i < packages.length; i++) {
            if (!delIdxs.contains(i)) {
                Set<Class<?>> _classes = ClassScaner.getClasses(loader, packages[i]);
                if (!_classes.isEmpty()) {
//                    if (aop != null) {
//                        for (Class cls : _classes) {
//                            aop.cache(cls.getTypeName(), cls);
//                        }
//                        aop.cache(packages[i], _classes);
//                    }
                    classes.addAll(_classes);
                }
            }
        }
        if (aop != null) {
            for (Class cls : classes) {
                Aspect aspect = (Aspect) cls.getAnnotation(Aspect.class);
                if (aspect != null) {
                    aop.registerAspect(cls);
                }
                if (!aop.isPrepared(cls)) {
                    aop.registerInterceptTarget(cls);
                }
            }
        }
        classes.forEach((Class<?> cls) -> {
            detectorMap.forEach((String name, IDetector detector) -> {
                detector.discern(cls);
            });
        });
        return classes;
    }

    /**
     * 初始化
     *
     * @param packages .分割的包名
     * @return 扫描到的类集合
     */
    final protected Set<Class<?>> scan(String... packages) {
        return scan(null, packages);
    }

}
