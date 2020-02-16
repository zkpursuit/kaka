package com.kaka.aopwear;

import com.kaka.aop.MethodInterceptor;
import com.kaka.aop.annotation.After;
import com.kaka.aop.annotation.AfterReturning;
import com.kaka.aop.annotation.AfterThrowing;
import com.kaka.aop.annotation.Around;
import com.kaka.aop.annotation.Before;
import com.kaka.aop.annotation.Intercept;
import com.kaka.aop.Aop;
import com.kaka.util.ClassScaner;
import com.kaka.util.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.cglib.proxy.Enhancer;

/**
 * 切面
 *
 * @author zkpursuit
 */
public class CglibAop extends Aop {

    /**
     * Class对应CgLib中的Enhancer对象
     */
    final Map<Class<?>, Enhancer> class_enhancer_map = new ConcurrentHashMap<>();

    /**
     * 方法完全限定名与其切面方法集合的映射
     */
    final Map<String, MethodAdvices> method_advices_map = new ConcurrentHashMap<>();

    /**
     * 类方法拦截器映射
     */
    final Map<String, MethodInterceptor> class_method_interceptor_map = new ConcurrentHashMap<>();

    final Map<Class<?>, MethodInterceptor> class_interceptor_map = new ConcurrentHashMap<>();

    private final Pattern pattern = Pattern.compile("\\(([\\s\\S]*)\\)");

    /**
     * 匹配方法参数
     *
     * @param src 类完全限定名方法名及其参数类型列表，参数类型列表为小括号限定且以逗号分隔
     * @return 方法参数
     */
    private String matchMethodParams(String src) {
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

//    /**
//     * 根据类名获取Class
//     *
//     * @param className
//     * @param classLoader
//     * @return
//     */
//    private Class<?> getClassByName(String className, ClassLoader classLoader) {
//        Class<?> clasz = classMap.get(className);
//        if (clasz == null) {
//            try {
//                clasz = classLoader.loadClass(className);
//                classMap.put(className, clasz);
//                Package _package = clasz.getPackage();
//                String packageName = _package.getName();
//                Set<Class<?>> classes = packageClassMap.get(packageName);
//                if (classes == null) {
//                    packageClassMap.put(packageName, classes);
//                } else {
//                    classes.add(clasz);
//                }
//            } catch (ClassNotFoundException ex) {
//            }
//        }
//        return clasz;
//    }

//    /**
//     * 根据包名获取类集合
//     *
//     * @param packageName 包名
//     * @param classLoader 类加载器
//     * @return 类集合
//     */
//    private Set<Class<?>> getClassesByPackageName(String packageName, ClassLoader classLoader) {
//        if (packageClassMap.containsKey(packageName)) {
//            return packageClassMap.get(packageName);
//        }
//        //提供的包名有父级包名已获得旗下的所有类
//        int idx;
//        String pname = packageName;
//        while ((idx = pname.lastIndexOf('.')) > 0) {
//            pname = pname.substring(0, idx);
//            if (packageClassMap.containsKey(pname)) {
//                return packageClassMap.get(pname);
//            }
//        }
//        //存在子级类包的映射则删除子集类包
//        Set<String> keys = packageClassMap.keySet();
//        for (String key : keys) {
//            if (key.startsWith(packageName)) {
//                packageClassMap.remove(key);
//            }
//        }
//        //获取本类包和旗下后代类包下的所有类
//        Set<Class<?>> _classes = ClassScaner.getClasses(classLoader, packageName);
//        if (!_classes.isEmpty()) {
//            packageClassMap.put(packageName, _classes);
//        }
//        return _classes;
//    }

    /**
     * 利用正则表达式匹配方法的完全限定名
     *
     * @param clasz           被切面的类
     * @param pattern         切点表达式的正则表示
     * @param classMethodsMap 被切面的类映射其被切面的方法
     */
    private void analyseAopClassMethod(Class clasz, Pattern pattern, Map<Class<?>, Set<Method>> classMethodsMap) {
        if (clasz == null) {
            return;
        }
        if (pattern == null) {
            return;
        }
        int modifiers = clasz.getModifiers();
        if (Modifier.isAbstract(modifiers)) {
            return;
        }
        if (Modifier.isInterface(modifiers)) {
            return;
        }
        Method[] methods = ReflectUtils.getDeclaredMethods(clasz, false);
        for (Method method : methods) {
            String methodGenName = method.toGenericString();
            int idx;
            while ((idx = methodGenName.indexOf(' ')) > 0) {
                methodGenName = methodGenName.substring(idx + 1);
            }
            Matcher matcher = pattern.matcher(methodGenName);
            if (matcher.matches()) {
                Set<Method> methodSet;
                if (!classMethodsMap.containsKey(clasz)) {
                    methodSet = new HashSet<>(methods.length);
                    classMethodsMap.put(clasz, methodSet);
                } else {
                    methodSet = classMethodsMap.get(clasz);
                }
                methodSet.add(method);
            }
        }
    }

    /**
     * 分析切点表达式
     *
     * @param pointcut    切点表达式
     * @param classLoader 类加载器
     * @return
     */
    private Map<Class<?>, Set<Method>> analyseAspectPointcut(String pointcut, ClassLoader classLoader) {
        pointcut = pointcut.replaceAll(" ", "");
        Map<Class<?>, Set<Method>> classMethodsMap = new HashMap<>();
        String methodParams = matchMethodParams(pointcut);
        if (methodParams != null) {
            methodParams = methodParams.trim();
            int idx = pointcut.lastIndexOf('(');
            String s = pointcut.substring(0, idx);
            idx = s.lastIndexOf('.');
            //String methodName = s.substring(idx + 1);
            if ("...".equals(methodParams)) {
                //同方法名的所有方法
                pointcut = pointcut.replace("(...)", "\\(&\\)");
                pointcut = pointcut.replaceAll("(\\*)|&", "([\\\\s\\\\S]*)");
                pointcut = pointcut.replaceAll("\\.", "\\\\.");
            } else if ("".equals(methodParams)) {
                //指定方法名的无参方法
                pointcut = pointcut.replace("()", "\\(\\)");
                pointcut = pointcut.replaceAll("\\*", "([\\\\s\\\\S]*)");
                pointcut = pointcut.replaceAll("\\.", "\\\\.");
            } else {
                //指定方法名指定参数的方法
                pointcut = pointcut.replace("(", "\\(");
                pointcut = pointcut.replace(")", "\\)");
                pointcut = pointcut.replaceAll("\\*", "([\\\\s\\\\S]*)");
                pointcut = pointcut.replaceAll("\\.", "\\\\.");
            }
            Pattern _pattern = Pattern.compile(pointcut);
            String fullClassName = s.substring(0, idx);
            char lastChar = fullClassName.charAt(fullClassName.length() - 1);
            if (lastChar == '*') {
                idx = fullClassName.lastIndexOf('.');
                String packageName = fullClassName.substring(0, idx);
                while ((idx = packageName.lastIndexOf(".*")) > 0) {
                    packageName = packageName.substring(0, idx);
                }
                if (!"".equals(packageName)) {
//                    Set<Class<?>> classes = getClassesByPackageName(packageName, classLoader);
                    Set<Class<?>> classes = ClassScaner.getClasses(classLoader, packageName);

                    classes.forEach((clasz) -> {
                        analyseAopClassMethod(clasz, _pattern, classMethodsMap);
                    });
                }
            } else {
//                Class<?> clasz = getClassByName(fullClassName, classLoader);
                Class<?> clasz;
                try {
                    clasz = classLoader.loadClass(fullClassName);
                } catch (ClassNotFoundException ex) {
                    clasz = null;
                }

                analyseAopClassMethod(clasz, _pattern, classMethodsMap);
            }
        }
        return classMethodsMap;
    }

    /**
     * 处理切面类和被切面类之间的各种映射关系
     *
     * @param classMethodsMap 被切面代理类和其所有将被代理方法的映射
     * @param aspectObject    切面对象
     * @param aspectMethod    切面对象中的切面方法
     * @param aspectAdvice    切面通知的字符串表示
     * @param order           切面通知执行优先级
     */
    private void aspectMethodMapping(Map<Class<?>, Set<Method>> classMethodsMap, Object aspectObject, Method aspectMethod, int order, String aspectAdvice) {
        if (classMethodsMap == null || classMethodsMap.isEmpty()) {
            return;
        }
        classMethodsMap.forEach((Class<?> cls, Set<Method> mtds) -> {
            for (Method m : mtds) {
                String methodGenName = m.toGenericString();
                MethodAdvices methodAdvices = method_advices_map.get(methodGenName);
                if (methodAdvices == null) {
                    methodAdvices = new MethodAdvices();
                    method_advices_map.put(methodGenName, methodAdvices);
                }
                MethodWrap methodWrap = new MethodWrap(aspectObject, aspectMethod, order);
                switch (aspectAdvice) {
                    case "before":
                        if (methodAdvices.before == null) {
                            methodAdvices.before = Collections.synchronizedList(new ArrayList<>());
                        }
                        methodAdvices.before.add(methodWrap);
                        sortMethodWraps(methodAdvices.before);
                        break;
                    case "after":
                        if (methodAdvices.after == null) {
                            methodAdvices.after = Collections.synchronizedList(new ArrayList<>());
                        }
                        methodAdvices.after.add(methodWrap);
                        sortMethodWraps(methodAdvices.after);
                        break;
                    case "afterReturning":
                        if (methodAdvices.afterReturning == null) {
                            methodAdvices.afterReturning = Collections.synchronizedList(new ArrayList<>());
                        }
                        methodAdvices.afterReturning.add(methodWrap);
                        sortMethodWraps(methodAdvices.afterReturning);
                        break;
                    case "around":
                        if (methodAdvices.around == null) {
                            methodAdvices.around = Collections.synchronizedList(new ArrayList<>());
                        }
                        methodAdvices.around.add(methodWrap);
                        sortMethodWraps(methodAdvices.afterThrowing);
                        break;
                    case "afterThrowing":
                        if (methodAdvices.afterThrowing == null) {
                            methodAdvices.afterThrowing = Collections.synchronizedList(new ArrayList<>());
                        }
                        methodAdvices.afterThrowing.add(methodWrap);
                        sortMethodWraps(methodAdvices.around);
                        break;
                }
            }
            if (!class_enhancer_map.containsKey(cls)) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(cls);
                enhancer.setCallback(new MethodAspectHandler(this));
                class_enhancer_map.put(cls, enhancer);
            }
        });
    }

    /**
     * 被切面代理方法上的切面通知处理器执行顺序调整
     *
     * @param list 切面通知集合
     */
    private void sortMethodWraps(List<MethodWrap> list) {
        if (list == null) {
            return;
        }
        if (list.isEmpty()) {
            return;
        }
        list.sort((MethodWrap wrap1, MethodWrap wrap2) -> {
            if (wrap2.order > wrap1.order) {
                return 1;
            }
            if (wrap2.order < wrap1.order) {
                return -1;
            }
            return 0;
        });
    }

    /**
     * 注册切面类
     *
     * @param aspectClass 切面类
     */
    @Override
    public void registerAspect(Class<?> aspectClass) {
        if (aspectClass == null) {
            return;
        }
        //创建切面对象
        Object aspectObject = ReflectUtils.newInstance(aspectClass);
        Method[] methods = ReflectUtils.getDeclaredMethods(aspectClass, true);
        ClassLoader classLoader = aspectClass.getClassLoader();
        for (Method aspectMethod : methods) {
            int paramCount = aspectMethod.getParameterCount();
            if (paramCount == 1 || paramCount == 2) {
                Class<?>[] paramTypes = aspectMethod.getParameterTypes();
                Class<?> paramType1 = paramTypes[0];
                if (JoinPoint.class.isAssignableFrom(paramType1)) {
                    if (paramCount == 1) {
                        if (paramType1 == JoinPoint.class) {
                            Before before = aspectMethod.getAnnotation(Before.class);
                            if (before != null) {
                                Map<Class<?>, Set<Method>> classMethodsMap = analyseAspectPointcut(before.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, before.order(), "before");
                            }
                            After after = aspectMethod.getAnnotation(After.class);
                            if (after != null) {
                                Map<Class<?>, Set<Method>> classMethodsMap = analyseAspectPointcut(after.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, after.order(), "after");
                            }
                            AfterReturning afterReturning = aspectMethod.getAnnotation(AfterReturning.class);
                            if (afterReturning != null) {
                                Map<Class<?>, Set<Method>> classMethodsMap = analyseAspectPointcut(afterReturning.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, afterReturning.order(), "afterReturning");
                            }
                        } else if (paramType1 == ProceedJoinPoint.class) {
                            Around around = aspectMethod.getAnnotation(Around.class);
                            if (around != null) {
                                Map<Class<?>, Set<Method>> classMethodsMap = analyseAspectPointcut(around.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, around.order(), "around");
                            }
                        }
                    } else {
                        if (paramType1 == JoinPoint.class) {
                            AfterThrowing afterThrowing = aspectMethod.getAnnotation(AfterThrowing.class);
                            if (afterThrowing != null) {
                                Map<Class<?>, Set<Method>> classMethodsMap = analyseAspectPointcut(afterThrowing.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, afterThrowing.order(), "afterThrowing");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据拦截器Class获得拦截器对象
     *
     * @param clasz 拦截器Class
     * @return 拦截器对象
     */
    private MethodInterceptor getInterceptor(Class<? extends MethodInterceptor> clasz) {
        if (!class_interceptor_map.containsKey(clasz)) {
            MethodInterceptor interceptor = (MethodInterceptor) ReflectUtils.newInstance(clasz);
            class_interceptor_map.put(clasz, interceptor);
            return interceptor;
        }
        return class_interceptor_map.get(clasz);
    }

    /**
     * 注册被拦截对象，被拦截对象的某些方法必然含有{@link com.kaka.aop.annotation.Intercept}注解
     *
     * @param targetClass 被拦截对象Class
     */
    @Override
    public final void registerInterceptTarget(Class<?> targetClass) {
        if (class_enhancer_map.containsKey(targetClass)) {
            return;
        }
        boolean hasAop = false;
        Method[] methods = ReflectUtils.getDeclaredMethods(targetClass, false);
        for (Method method : methods) {
            Intercept intercept = method.getAnnotation(Intercept.class);
            if (intercept != null) {
                hasAop = true;
                MethodInterceptor interceptor = getInterceptor(intercept.value());
                class_method_interceptor_map.put(method.toGenericString(), interceptor);
            }
        }
        if (hasAop) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(targetClass);
            enhancer.setCallback(new MethodInterceptHandler(this));
            class_enhancer_map.put(targetClass, enhancer);
        }
    }

    @Override
    public final boolean isPrepared(Class<?> clasz) {
        return class_enhancer_map.containsKey(clasz);
    }

    /**
     * 创建被切面对象的实例
     *
     * @param clasz 被切面代理对象Class
     * @return 被切面代理对象
     */
    @Override
    public final Object createInstance(Class clasz) {
        Enhancer enhancer = class_enhancer_map.get(clasz);
        if (enhancer != null) {
            return enhancer.create();
        }
        return null;
    }

}
