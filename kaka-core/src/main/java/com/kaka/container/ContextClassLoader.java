package com.kaka.container;

import com.kaka.aop.Aop;
import com.kaka.aop.AopFactory;
import com.kaka.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 应用程序运行容器、插件加载器
 *
 * @author zkpursuit
 */
public class ContextClassLoader extends URLClassLoader {

    /**
     * 默认系统资源
     */
    final static String[] __defaultSysResource
            = {
            "sun.",
            "sun.misc",
            "java.",
            "javax.",
            "com.kaka.container."
    };

    static {
        registerAsParallelCapable();
    }

    private static final Logger LOG = Logger.getLogger(ContextClassLoader.class.getTypeName());

    /**
     * 父级加载器
     */
    private final ClassLoader _parent;
    /**
     * 受支持的class文件集合压缩包的扩展，支持jar、zip
     */
    private final Set<String> _extensions = new HashSet<>();
    /**
     * 加载器名
     */
    private String _name = String.valueOf(hashCode());
    /**
     * 类文件转换器
     */
    private final List<ClassFileTransformer> _transformers = new CopyOnWriteArrayList<>();
    /**
     * 系统类路径
     */
    private final Set<String> systemResources = new HashSet<>();
    /**
     * 是否优先父加载器
     */
    private final boolean isParentLoaderPriority;

    /**
     * 构造方法
     *
     * @param parent               父级classloader
     * @param parentLoaderPriority 父级加载器是否优先加载
     *                             <p> true，优先从父级加载器加载且尽可能的忽略本级下的class，一般情况用于插件代码的加载。
     *                             插件编写规则：尽可能的把公共代码放在主应用中，插件外链引用公共代码，且仅包含其本身的核心代码。
     *                             其它情况以保证执行无错为标准。
     *                             <p> false，优先从本加载器加载且尽可能的忽略父级下的class，一般情况用于运行独立的应用。
     *                             addClassPath加载独立应用的classpath。
     *                             addJars加载独立应用所引用的jar包。
     * @throws IOException 如果不能初始化，则抛出IOException
     */
    public ContextClassLoader(ClassLoader parent, boolean parentLoaderPriority) throws IOException {
        super(new URL[]{}, parent != null ? parent
                : (Thread.currentThread().getContextClassLoader() != null ? Thread.currentThread().getContextClassLoader()
                : (ContextClassLoader.class.getClassLoader() != null ? ContextClassLoader.class.getClassLoader()
                : ClassLoader.getSystemClassLoader())));
        this.isParentLoaderPriority = parentLoaderPriority;
        _parent = getParent();
        if (_parent == null) {
            throw new IllegalArgumentException("no parent classloader!");
        }
        _extensions.add(".jar");
        _extensions.add(".zip");
        this.addSystemResource(__defaultSysResource);
        String extensions = System.getProperty(ContextClassLoader.class.getName() + ".extensions");
        if (extensions != null) {
            StringTokenizer tokenizer = new StringTokenizer(extensions, ",;");
            while (tokenizer.hasMoreTokens()) {
                _extensions.add(tokenizer.nextToken().trim());
            }
        }
    }

    /**
     * 构造方法
     *
     * @param parent 父级classloader
     * @throws IOException
     */
    public ContextClassLoader(ClassLoader parent) throws IOException {
        this(parent, false);
    }

    /**
     * 构造方法
     *
     * @param parentLoaderPriority 父级加载器是否优先加载
     * @throws IOException
     */
    public ContextClassLoader(boolean parentLoaderPriority) throws IOException {
        this(null, parentLoaderPriority);
    }

    /**
     * 构造方法
     *
     * @throws IOException
     */
    public ContextClassLoader() throws IOException {
        this(false);
    }

    /**
     * @return 加载器名
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name 设置加载器名
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * 添加系统资源路径
     *
     * @param resource
     */
    final public void addSystemResource(String... resource) {
        if (resource.length == 0) {
            return;
        }
        List<String> list = new ArrayList<>(resource.length);
        for (String res : resource) {
            res = res.replaceAll("[/|\\\\]+", ".");
            char last = res.charAt(res.length() - 1);
            if (last != '.') {
                res += ".";
            }
            list.add(res);
        }
        systemResources.addAll(list);
    }

    /**
     * 是否为系统资源
     *
     * @param name
     * @param url
     * @return
     */
    boolean isSystemResource(String name, URL url) {
        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - 6);
        }
        name = name.replace("/", ".");
        boolean nameFlag = false;
        for (String classes : systemResources) {
            if (name.startsWith(classes)) {
                nameFlag = true;
                break;
            }
        }
        String urlPath = url.getPath();
        urlPath = urlPath.replace('/', '.');
        boolean urlFlag = false;
        for (String classes : systemResources) {
            if (urlPath.contains(classes)) {
                urlFlag = true;
                break;
            }
        }
        return nameFlag || urlFlag;
    }

    /**
     * 是否为系统类
     *
     * @param cls
     * @return true为系统类
     */
    boolean isSystemClass(Class cls) {
        Package pack = cls.getPackage();
        String packName = pack.getName();
        for (String classes : systemResources) {
            if (packName.startsWith(classes)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加classpath
     *
     * @param classPath classpath路径文件
     * @throws IOException
     */
    final public void addClassPath(File classPath) throws IOException {
        if (classPath.isDirectory()) {
            addClassPath(classPath.getAbsolutePath());
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * 添加classpath
     *
     * @param classPath classpath路径文件
     * @throws IOException
     */
    private void addClassPath(String classPath) throws IOException {
        if (classPath == null) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(classPath, ",;");
        while (tokenizer.hasMoreTokens()) {
            File file = new File(tokenizer.nextToken().trim());
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Path resource=" + file);
            }
            addURL(file.toURI().toURL());
        }
    }

    /**
     * @param file 检查文件是否受支持，主要针对jar文件和打包class的zip文件
     */
    private boolean isFileSupported(String file) {
        int dot = file.lastIndexOf('.');
        return dot != -1 && _extensions.contains(file.substring(dot));
    }

    /**
     * 添加jar或者打包class后的zip文件
     *
     * @param lib jar或者zip所在目录
     */
    public void addJars(File lib) {
        if (lib.exists() && lib.isDirectory()) {
            File[] files = lib.listFiles();
            if (files != null) {
                Arrays.sort(files);
            }
            for (int f = 0; files != null && f < files.length; f++) {
                try {
                    File fn = files[f];
                    if (LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "addJar - {}", fn);
                    }
                    String fnlc = fn.getName().toLowerCase(Locale.ENGLISH);
                    if (isFileSupported(fnlc)) {
                        String jarFilePath = fn.getAbsolutePath();
                        addClassPath(jarFilePath);
                    }
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public PermissionCollection getPermissions(CodeSource cs) {
        //PermissionCollection permissions = _context.getPermissions();
        //PermissionCollection pc = (permissions == null) ? super.getPermissions(cs) : permissions;
        //return pc;
        return super.getPermissions(cs);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> from_parent = new ArrayList<>();
        List<URL> from_app = new ArrayList<>();

        Enumeration<URL> urls = _parent.getResources(name);
        while (urls != null && urls.hasMoreElements()) {
            URL url = urls.nextElement();
            from_parent.add(url);
        }

        urls = this.findResources(name);
        while (urls != null && urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (!isSystemResource(name, url) || from_parent.isEmpty()) {
                from_app.add(url);
            }
        }

        List<URL> resources;

        if (isParentLoaderPriority) {
            from_parent.addAll(from_app);
            resources = from_parent;
        } else {
            from_app.addAll(from_parent);
            resources = from_app;
        }

        if (LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "getResources {} {}", new Object[]{name, resources});
        }

        return Collections.enumeration(resources);
    }

    @Override
    public URL getResource(String name) {
        URL resource = null;
        if (isParentLoaderPriority) {
            URL parent_url = _parent.getResource(name);
            if (parent_url != null) {
                //if (parent_url != null && (Boolean.TRUE.equals(__loadServerClasses.get()) || !isSystemResource(name, parent_url))) {
                resource = parent_url;
            } else {
                URL app_url = this.findResource(name);
                if (app_url != null) {
                    resource = app_url;
                }
            }
        } else {
            URL app_url = this.findResource(name);
            //isSystemResource判断，因为系统资源在父级已加载完，故而，
            //此方法为true时直接走else从父级加载器中查找资源
            if (app_url != null && !isSystemResource(name, app_url)) {
                resource = app_url;
            } else {
                // 尝试从父级loader中查找资源
                URL parent_url = _parent.getResource(name);
                if (parent_url != null && isSystemResource(name, parent_url)) {
                    //if (parent_url != null && (Boolean.TRUE.equals(__loadServerClasses.get()) || !isSystemResource(name, parent_url))) {
                    resource = parent_url;
                } else if (app_url != null) {
                    resource = app_url;
                }
            }
        }
        if (resource == null && name.startsWith("/")) {
            resource = getResource(name.substring(1));
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "getResources {} {}", new Object[]{name, resource});
        }
        return resource;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            ClassNotFoundException ex = null;
            Class<?> parent_class;
            Class<?> app_class;
            //类是否已经被加载
            app_class = findLoadedClass(name);
            if (app_class != null) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, "found app loaded {}", app_class);
                }
                return app_class;
            }
            if (isParentLoaderPriority) {
                //尝试从父级loader加载
                try {
                    parent_class = _parent.loadClass(name);
                    //if (isSystemClass(parent_class)) {
                    if (LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "PLP parent loaded {}", parent_class);
                    }
                    return parent_class;
                    //}
                } catch (ClassNotFoundException e) {
                    ex = e;
                }
                try {
                    app_class = this.findClass(name);
                    resolveClass(app_class);
                    if (LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "PLP app loaded {}", app_class);
                    }
                    return app_class;
                } catch (ClassNotFoundException e) {
                    if (ex == null) {
                        ex = e;
                    } else {
                        ex.addSuppressed(e);
                    }
                }
                throw ex;
            } else {
                String path = name.replace('.', '/').concat(".class");
                URL app_url = findResource(path);
                //从本级加载
                if (app_url != null) {
                    if (!isSystemResource(name, app_url)) {
                        app_class = this.foundClass(name, app_url);
                        resolveClass(app_class);
                        if (LOG.isLoggable(Level.FINER)) {
                            LOG.log(Level.FINER, "WAP app loaded {}", app_class);
                        }
                        return app_class;
                    }
                }
                //尝试从父级loader加载
                try {
                    parent_class = _parent.loadClass(name);
                    //if (isSystemClass(parent_class)) {
                    if (LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "WAP parent loaded {}", parent_class);
                    }
                    return parent_class;
                    //}
                } catch (ClassNotFoundException e) {
                    ex = e;
                }
                //父级loader中未找到就在本级中找
                if (app_url != null) {
                    app_class = this.foundClass(name, app_url);
                    resolveClass(app_class);
                    if (LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "WAP !server app loaded {}", app_class);
                    }
                    return app_class;
                }
                throw ex == null ? new ClassNotFoundException(name) : ex;
            }
        }
    }

    /**
     * 添加类文件转换器，如果class文件经过加密压缩等处理，可以调用此方法传入参数进行解密解压缩等操作转换
     *
     * @param transformer
     */
    public void addTransformer(ClassFileTransformer transformer) {
        _transformers.add(transformer);
    }

    /**
     * 移出类文件转换器
     *
     * @param transformer
     * @return
     */
    public boolean removeTransformer(ClassFileTransformer transformer) {
        return _transformers.remove(transformer);
    }

    protected Class<?> foundClass(final String name, URL url) throws ClassNotFoundException {
        if (_transformers.isEmpty()) {
            return super.findClass(name);
        }
        try (InputStream content = url.openStream()) {
            byte[] bytes = IOUtils.readBytes(content);
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "foundClass({}) url={} cl={}", new Object[]{name, url, this});
            }
            for (ClassFileTransformer transformer : _transformers) {
                byte[] tmp = transformer.transform(this, name, null, null, bytes);
                if (tmp != null) {
                    bytes = tmp;
                }
            }
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException | IllegalClassFormatException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (_transformers.isEmpty()) {
            return super.findClass(name);
        }
        String path = name.replace('.', '/').concat(".class");
        URL url = findResource(path);
        if (url == null) {
            throw new ClassNotFoundException(name);
        }
        return foundClass(name, url);
    }

    @Override
    public void close() throws IOException {
        Aop aop = AopFactory.getAop();
        if (aop != null) {
            aop.unload(this);
        }
        super.close();
    }

    @Override
    public String toString() {
        return "ContextClassLoader=" + _name + "@" + Long.toHexString(hashCode());
    }

}
