package com.test;

import com.kaka.Startup;
import com.kaka.container.ContextClassLoader;
import com.kaka.notice.Message;
import com.kaka.util.ResourceUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.kaka.notice.Facade.facade;

public class TestContextClassLoader extends Startup {

    public static void main(String[] args) throws Exception {
        //runApp();
        runPlugin();

//        Map<Integer, String> map = new HashMap<>();
//        map.put(1, "a");
//        map.put(2, "b");
//        map.put(3, "c");
//        Set<Integer> keys = map.keySet();
//        Iterator<Integer> iterator = keys.iterator();
//        while(iterator.hasNext()) {
//            Integer key = iterator.next();
//            if(key == 2) {
//                iterator.remove();
//            }
//        }
//        System.out.println(map.size());
//        map.forEach((Integer k, String v) -> {
//            System.out.println(k + "  " + v);
//        });
//
//        Integer[] array = new Integer[keys.size()];
//        keys.toArray(array);
//        for(Integer key : array) {
//            if(key == 2) {
//                map.remove(key);
//            }
//        }
//        System.out.println(map.size());
    }

    public static String getPath() {
        String classRootPath = ResourceUtils.getClassLoaderPath(TestContextClassLoader.class);
        String path;
        if (classRootPath.endsWith("classes")) {
            File file = new File(classRootPath);
            file = file.getParentFile().getParentFile();
            path = file.getAbsolutePath().replaceAll("\\\\", "/");
        } else {
            path = classRootPath.replaceAll("\\\\", "/");
        }
        path = path + "/context";
        return path;
    }

    /**
     * 加载外部的classes和lib运行独立的应用程序
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    static void runApp() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String path = getPath();
        ContextClassLoader loader = new ContextClassLoader(false);
        Thread.currentThread().setContextClassLoader(loader);
        //ClassFileTransformer可对class进行解密
        //loader.addTransformer(new PluginClassFileTransformer());
        //添加应用所依赖的jar
        loader.addJars(new File(path + "/app/lib"));
        //添加应用的classpath
        loader.addClassPath(new File(path + "/app/classes"));
        //获得应用主类
        Class cls = loader.loadClass("com.http.Main");
        //反射执行应用的main方法
        Method method = cls.getDeclaredMethod("main", String[].class);
        method.invoke(null, (Object) new String[]{});
    }

    static class PluginStartup extends Startup {

        public void init(ClassLoader loader, String[] packages) {
            scan(loader, packages);
        }

    }

    /**
     * 加载插件
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws Exception
     */
    static void runPlugin() throws IOException, IllegalArgumentException, Exception {
        String path = getPath();
        ClassLoader parent_loader = Thread.currentThread().getContextClassLoader();
        ContextClassLoader loader = new ContextClassLoader(parent_loader, true);
        loader.addClassPath(new File(path + "/plugin/classes"));
        List<String> packs = new ArrayList<>();
        InputStream is = loader.getResourceAsStream("scan.pack"); //插件中所需要扫描的类包
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null && !"".equals(line)) {
                line = line.trim();
                packs.add(line);
            }
        }
        String[] packages = new String[packs.size()];
        packs.toArray(packages);
        PluginStartup startup = new PluginStartup();
        //扫描插件中的类包
        startup.init(loader, packages);

        // Facade.unloadAll(loader);
        // Aop aop = AopFactory.getAop();
        // if(aop != null) {
        //     aop.unload(loader);
        // }

        //向插件中的Command发送事件通知
        facade.sendMessage(new Message("PluginCommand"));
    }

}
