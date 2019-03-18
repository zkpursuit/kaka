package com.kaka.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描工具
 *
 * @author zkpursuit
 */
public class ClassScaner {

    /**
     * 获取包下的所有类
     *
     * @param loader 查询类的类加载器
     * @param packageName 类包名
     * @return 包下所有的类
     */
    public static Set<Class<?>> getClasses(ClassLoader loader, String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        getClasses(loader, packageName, true, classes);
        return classes;
    }

    /**
     * 从包pkgName中获取所有的Class
     *
     * @param loader 类加载器
     * @param packageName 包名
     * @param loaderGetResourcesParam 类加载器getResources中是否传入pkgName对应的相对路径参数
     * @param classes 找到的类的集合
     */
    private static void getClasses(ClassLoader loader, String packageName, boolean loaderGetResourcesParam, Set<Class<?>> classes) {
        try {
            String _pkgName = packageName;
            String packageDirName = packageName.replace('.', '/');
            Enumeration<URL> dirs;
            if (loaderGetResourcesParam) {
                dirs = loader.getResources(packageDirName);
            } else {
                dirs = loader.getResources("");
            }
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                //System.out.println("------------------------------->>" + url);
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    if (!loaderGetResourcesParam) {
                        filePath = filePath + "/" + packageDirName;
                    }
                    findClasses(loader, _pkgName, filePath, true, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                String pn = packageDirName;
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    pn = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if (idx != -1) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(pn.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(loader.loadClass(pn + '.' + className));
                                        } catch (ClassNotFoundException | NoClassDefFoundError | ClassFormatError ex) {
                                            throw new Error(ex);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException ex) {
                        throw ex;
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            throw new Error(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

//    /**
//     * 获取refClass所运行的当前项目的类加载器中所加载的类
//     *
//     * @param refClass 参考类
//     * @param startFromRoot 是否从类所在的根包遍历，true为是
//     * @return 类集合
//     */
//    public static Set<Class<?>> getClasses(Class<?> refClass, boolean startFromRoot) {
//        Set<Class<?>> classes = new LinkedHashSet<>();
//        Package pkg = refClass.getPackage();
//        String pkgName = pkg.getName();
//        if (startFromRoot) {
//            int index = pkgName.indexOf(".");
//            if (index > 0) {
//                pkgName = pkgName.substring(0, index);
//            }
//        }
//        ClassLoader loader = refClass.getClassLoader();
//        getClasses(pkgName, loader, false, classes);
//        return classes;
//    }
//
//    /**
//     * 获取refClass所运行的当前项目的类加载器中所加载的类
//     *
//     * @param refClass 参考类
//     * @return 类集合
//     */
//    public static Set<Class<?>> getClasses(Class<?> refClass) {
//        return getClasses(refClass, true);
//    }
    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param loader 类加载器
     * @param packageName 包名
     * @param packagePath 包名对应的绝对路径
     * @param recursive 是否递归遍历子孙包
     * @param classes 类集合
     */
    private static void findClasses(ClassLoader loader, String packageName, final String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles((File file) -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClasses(loader, packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
                loader.setClassAssertionStatus(packageName, recursive);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(loader.loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException | NoClassDefFoundError | ClassFormatError ex) {
                    throw new Error(ex);
                }
            }
        }
    }
}
