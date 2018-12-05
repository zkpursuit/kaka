package com.kaka.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 资源路径
 *
 * @author zkpursuit
 */
public class ResourceUtils {

    /**
     * 获取项目工程根目录
     *
     * @return 项目根目录
     */
    public static String getProjectDirectroyPath() {
        try {
            return new File("").getCanonicalPath();
        } catch (IOException ex) {
        }
        return System.getProperty("user.dir");
    }

    /**
     * -----------------------------------------------------------------------
     * getClassLoaderPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
     * Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *
     * class.getProtectionDomain().getCodeSource().getLocation()
     *
     * @param cls 为Class类型
     * @return 返回值为该类所在的Java程序运行的目录,如果该类在某个jar包中，则返回此jar包的父级目录
     * -------------------------------------------------------------------------
     */
    public static String getClassLoaderPath(Class<?> cls) {
        //检查用户传入的参数是否为空
        if (cls == null) {
            throw new java.lang.IllegalArgumentException("参数不能为空！");
        }
        ClassLoader loader = cls.getClassLoader();
        //获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        //获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        //如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            //此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith("java.") || packName.startsWith("javax.")) {
                throw new java.lang.IllegalArgumentException("不要传送系统类！");
            }
            //在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(packName.length() + 1);
            if (!packName.contains(".")) {
                path = packName + "/";
            } else {
                path = packName.replace('.', '/') + "/";
            }
        }
        //调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(path + clsName);
        //从URL对象中获取路径信息
        String realPath = url.getPath();
        //去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        //去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);
        //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;
    }

    /**
     * 获取文件流
     *
     * @param fileName 文件名
     * @param cls 参考类
     * @return 文件输入流
     */
    public final static InputStream getResourceAsStream(String fileName, Class<?> cls) {
        //cls包下读取
        InputStream is = null;
        try {
            is = cls.getResourceAsStream(fileName);
        } catch (Exception ex) {
        }
        if (is == null) {
            //cls根包下读取
            try {
                is = cls.getClassLoader().getResourceAsStream(fileName);
            } catch (Exception ex) {
            }
        }
        if (is == null) {
            //编译后的classes文件夹下
            try {
                is = cls.getProtectionDomain().getClassLoader().getResourceAsStream(fileName);
            } catch (Exception ex) {
            }
        }
        if (is == null) {
            try {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            } catch (Exception ex) {
            }
        }
        if (is == null) {
            String path = getClassLoaderPath(cls);
            String filePath = path + "/" + fileName;
            try {
                is = new FileInputStream(filePath);
            } catch (FileNotFoundException ex) {
                //再找不到就往父级查找，如果父级还是没有，则返回null
                File f = new File(path);
                if (f.exists()) {
                    path = f.getParent();
                    filePath = path + "/" + fileName;
                    try {
                        is = new FileInputStream(filePath);
                    } catch (FileNotFoundException e) {
                    }
                }
            }
        }
        return is;
    }

}
