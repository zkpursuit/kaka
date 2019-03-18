package com.kaka.aopwear;

import com.kaka.aop.MethodInterceptor;
import com.kaka.aop.annotation.After;
import com.kaka.aop.annotation.AfterReturning;
import com.kaka.aop.annotation.AfterThrowing;
import com.kaka.aop.annotation.Around;
import com.kaka.aop.annotation.Before;
import com.kaka.aop.annotation.Intercept;
import com.kaka.aop.AbstractAop;
import com.kaka.util.ClassScaner;
import com.kaka.util.ReflectUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.cglib.proxy.Enhancer;

/**
 * 切面
 *
 * @author zkpursuit
 */
public class CglibAop extends AbstractAop {

    /**
     * Class对应CgLib中的Enhancer对象
     */
    final Map<Class<?>, Enhancer> class_enhancer_map = new ConcurrentHashMap<>();
    /**
     * Class对应《方法对应切面方法信息》
     */
    final Map<Class<?>, Map<Method, MethodAdvices>> class_aspect_method_map = new ConcurrentHashMap<>();

    /**
     * 类方法拦截器映射
     */
    final Map<String, MethodInterceptor> class_method_interceptor_map = new ConcurrentHashMap<>();

    final Map<Class<?>, MethodInterceptor> class_interceptor_map = new ConcurrentHashMap<>();

    public final boolean isRegistered(Class<?> clasz) {
        return class_enhancer_map.containsKey(clasz);
    }

    private final Pattern pattern = Pattern.compile("\\(([\\s\\S]*)\\)");

    /**
     * 匹配方法参数
     *
     * @param src 类完全限定名方法名及其参数类型列表，参数类型列表为小括号限定且以逗号分隔
     * @return 方法参数
     */
    private String matchMethodParamsFromAopPointcut(String src) {
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 根据类名获取Class
     *
     * @param className
     * @param classLoader
     * @return
     */
    private Class<?> getClassByName(String className, ClassLoader classLoader) {
        if (className.startsWith("com.kaka")) {
            return null;
        }
        Class<?> clasz = classMap.get(className);
        if (clasz == null) {
            try {
                clasz = classLoader.loadClass(className);
                classMap.put(className, clasz);
                Package _package = clasz.getPackage();
                String packageName = _package.getName();
                Set<Class<?>> classes = packageClassMap.get(packageName);
                if (classes == null) {
                    packageClassMap.put(packageName, classes);
                } else {
                    classes.add(clasz);
                }
            } catch (ClassNotFoundException ex) {
            }
        }
        return clasz;
    }

    /**
     * 根据包名获取类集合
     *
     * @param packageName 包名
     * @param classLoader 类加载器
     * @return 类集合
     */
    private Set<Class<?>> getClassesByPackageName(String packageName, ClassLoader classLoader) {
        if (packageName.startsWith("com.kaka")) {
            return null;
        }
        Set<Class<?>> classes = packageClassMap.get(packageName);
        if (classes == null) {
            Set<Class<?>> _classes = ClassScaner.getClasses(classLoader, packageName);
            if (!_classes.isEmpty()) {
                packageClassMap.put(packageName, _classes);
            }
            classes = _classes;
        }
        return classes;
    }

    /**
     * 根据类名，获取对应的Class
     *
     * @param className 类名
     * @param classLoader 类加载器
     * @return Class
     */
    private Class getTypeByName(String className, ClassLoader classLoader) {
        switch (className) {
            case "byte":
                return byte.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "Byte":
                return Byte.class;
            case "Boolean":
                return Boolean.class;
            case "Character":
                return Character.class;
            case "Short":
                return Short.class;
            case "Integer":
                return Integer.class;
            case "Long":
                return Long.class;
            case "Float":
                return Float.class;
            case "Double":
                return Double.class;
            case "String":
                return String.class;
        }
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

    /**
     * 分析出被切面代理类的哪些方法需要被代理
     *
     * @param clasz 被切面代理的类
     * @param methodName 被代理方法名
     * @param methodParams 被代理方法参数
     * @param classMethodsMap 被切面代理类和其所有将被代理方法的映射
     */
    private void analyseAopClassMethod(Class clasz, String methodName, String methodParams, Map<Class<?>, Method[]> classMethodsMap) {
        if (clasz == null) {
            return;
        }
        if ("...".equals(methodParams)) {
            //同方法名的所有方法
            Method[] methods = ReflectUtils.getDeclaredMethods(clasz, false);
            List<Method> list = new ArrayList<>(methods.length);
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    list.add(method);
                }
            }
            if (!list.isEmpty()) {
                classMethodsMap.put(clasz, list.toArray(new Method[list.size()]));
            }
        } else if ("".equals(methodParams)) {
            //指定方法名的无参方法
            Method method = ReflectUtils.getDeclaredMethod(clasz, methodName, new Class[]{});
            if (method != null) {
                classMethodsMap.put(clasz, new Method[]{method});
            }
        } else {
            //指定方法名指定参数的方法
            String[] paramTypes = methodParams.split(",");
            Class[] paramTypeClasses = new Class[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                paramTypeClasses[i] = getTypeByName(paramTypes[i], clasz.getClassLoader());
            }
            Method[] methods = new Method[]{ReflectUtils.getDeclaredMethod(clasz, methodName, paramTypeClasses)};
            classMethodsMap.put(clasz, methods);
        }
    }

    /**
     * 分析aop切入点表达式
     *
     * @param pointcut aop切入点表达式
     * @param classLoader 类加载器
     */
    private Map<Class<?>, Method[]> analyseAspectPointcut(String pointcut, ClassLoader classLoader) {
        //判断是否含有小括号，含有则为方法名
        Map<Class<?>, Method[]> classMethodsMap = new HashMap<>();
        String methodParams = matchMethodParamsFromAopPointcut(pointcut);
        if (methodParams != null) {
            methodParams = methodParams.trim();
            int idx = pointcut.lastIndexOf('(');
            String s = pointcut.substring(0, idx);
            idx = s.lastIndexOf('.');
            String methodName = s.substring(idx + 1);
            String className = s.substring(0, idx);
            if (className.lastIndexOf('*') >= 0) {
                idx = className.lastIndexOf('.');
                String packageName = className.substring(0, idx);
                Set<Class<?>> classes = getClassesByPackageName(packageName, classLoader);
                for (Class clasz : classes) {
                    analyseAopClassMethod(clasz, methodName, methodParams, classMethodsMap);
                }
            } else {
                Class<?> clasz = getClassByName(className, classLoader);
                analyseAopClassMethod(clasz, methodName, methodParams, classMethodsMap);
            }
        }
        return classMethodsMap;
    }

    /**
     * 处理切面类和被切面类之间的各种映射关系
     *
     * @param classMethodsMap 被切面代理类和其所有将被代理方法的映射
     * @param aspectObject 切面对象
     * @param aspectMethod 切面对象中的切面方法
     * @param aspectAdvice 切面通知的字符串表示
     * @param order 切面通知执行优先级
     */
    private void aspectMethodMapping(Map<Class<?>, Method[]> classMethodsMap, Object aspectObject, Method aspectMethod, String aspectAdvice, int order) {
        if (classMethodsMap == null || classMethodsMap.isEmpty()) {
            return;
        }
        classMethodsMap.forEach((Class<?> cls, Method[] mtds) -> {
            Map<Method, MethodAdvices> methodMap = class_aspect_method_map.get(cls);
            if (methodMap == null) {
                methodMap = new HashMap<>();
                class_aspect_method_map.put(cls, methodMap);
            }
            for (Method m : mtds) {
                MethodAdvices methodAdvices = methodMap.get(m);
                if (methodAdvices == null) {
                    methodAdvices = new MethodAdvices();
                    methodMap.put(m, methodAdvices);
                }
                switch (aspectAdvice) {
                    case "before":
                        if (methodAdvices.before == null) {
                            methodAdvices.before = new ArrayList<>(8);
                        }
                        methodAdvices.before.add(new MethodWrap(aspectObject, aspectMethod, order));
                        break;
                    case "after":
                        if (methodAdvices.after == null) {
                            methodAdvices.after = new ArrayList<>(8);
                        }
                        methodAdvices.after.add(new MethodWrap(aspectObject, aspectMethod, order));
                        break;
                    case "afterReturning":
                        if (methodAdvices.afterReturning == null) {
                            methodAdvices.afterReturning = new ArrayList<>(8);
                        }
                        methodAdvices.afterReturning.add(new MethodWrap(aspectObject, aspectMethod, order));
                        break;
                    case "around":
                        if (methodAdvices.around == null) {
                            methodAdvices.around = new ArrayList<>(8);
                        }
                        methodAdvices.around.add(new MethodWrap(aspectObject, aspectMethod, order));
                        break;
                    case "afterThrowing":
                        if (methodAdvices.afterThrowing == null) {
                            methodAdvices.afterThrowing = new ArrayList<>(8);
                        }
                        methodAdvices.afterThrowing.add(new MethodWrap(aspectObject, aspectMethod, order));
                        break;
                }
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
                                Map<Class<?>, Method[]> classMethodsMap = analyseAspectPointcut(before.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, "before", before.order());
                            }
                            After after = aspectMethod.getAnnotation(After.class);
                            if (after != null) {
                                Map<Class<?>, Method[]> classMethodsMap = analyseAspectPointcut(after.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, "after", after.order());
                            }
                            AfterReturning afterReturning = aspectMethod.getAnnotation(AfterReturning.class);
                            if (afterReturning != null) {
                                Map<Class<?>, Method[]> classMethodsMap = analyseAspectPointcut(afterReturning.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, "afterReturning", afterReturning.order());
                            }
                        } else if (paramType1 == ProceedJoinPoint.class) {
                            Around around = aspectMethod.getAnnotation(Around.class);
                            if (around != null) {
                                Map<Class<?>, Method[]> classMethodsMap = analyseAspectPointcut(around.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, "around", around.order());
                            }
                        }
                    } else {
                        if (paramType1 == JoinPoint.class) {
                            AfterThrowing afterThrowing = aspectMethod.getAnnotation(AfterThrowing.class);
                            if (afterThrowing != null) {
                                Map<Class<?>, Method[]> classMethodsMap = analyseAspectPointcut(afterThrowing.value(), classLoader);
                                aspectMethodMapping(classMethodsMap, aspectObject, aspectMethod, "afterThrowing", afterThrowing.order());
                            }
                        }
                    }
                }
            }
        }
        class_aspect_method_map.forEach((Class<?> clasz, Map<Method, MethodAdvices> map) -> {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clasz);
            enhancer.setCallback(new MethodAspectHandler(this));
            class_enhancer_map.put(clasz, enhancer);
            map.forEach((Method m, MethodAdvices advices) -> {
                sortMethodWraps(advices.before);
                sortMethodWraps(advices.after);
                sortMethodWraps(advices.afterReturning);
                sortMethodWraps(advices.afterThrowing);
                sortMethodWraps(advices.around);
            });
        });
    }

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
