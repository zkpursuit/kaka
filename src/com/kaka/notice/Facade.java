package com.kaka.notice;

import com.kaka.util.ReflectUtils;
import com.kaka.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 整个框架的中枢神经（大脑）
 *
 * @author zkpursuit
 */
public class Facade implements INotifier {

    private static final Map<String, Facade> instanceMap = new HashMap<>();

    public synchronized static Facade getInstance(String key) {
        Facade inst;
        if (instanceMap.get(key) == null) {
            try {
                inst = new Facade(key);
            } catch (Exception e) {
                inst = instanceMap.get(key);
            }
        } else {
            inst = instanceMap.get(key);
        }
        return inst;
    }

    /**
     * 唯一默认实例
     */
    public final static Facade facade = getInstance("default");

    public static Facade getInstance() {
        return facade;
    }

    public synchronized static boolean hasCore(String key) {
        return instanceMap.containsKey(key);
    }

    public synchronized static void removeCore(String key) {
        Facade inst = instanceMap.remove(key);
        if (inst != null) {
            inst.dispose();
        }
    }

    private String __name;
    private final Map<String, Proxy> proxyMap = new ConcurrentHashMap<>();
    private final Map<String, Mediator> mediaMap = new ConcurrentHashMap<>();
    private final Map<Object, List<Mediator>> notiMediMap = new ConcurrentHashMap<>();
    private final Map<Object, CommandPool> cmdPoolMap = new ConcurrentHashMap<>();
    private Executor threadPool;
    private ScheduledExecutorService scheduleThreadPool;
    private final Map<String, ScheduledFuture<?>> scheduleFutureMap = new ConcurrentHashMap<>();

    /**
     * 创建一个内核
     *
     * @param key 内核唯一标识名
     */
    private Facade(String key) {
        this.init(key);
    }

    /**
     * 初始化内核
     *
     * @param key 内核唯一标识名
     */
    private void init(String key) {
        if (instanceMap.get(key) != null) {
            throw new RuntimeException(String.format("%s 对应的实例已被创建", key));
        }
        this.__name = key;
        instanceMap.put(key, this);
    }

    /**
     * 获取内核唯一标识名
     *
     * @return 内核唯一标识名
     */
    public String getName() {
        return this.__name;
    }

    /**
     * 初始化线程池，用于sendMessage中异步处理消息
     *
     * @param threadPool 线程池
     */
    public void initThreadPool(Executor threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * 异步定时调度线程池
     *
     * @param scheduleThreadPool 定时调度线程池
     */
    public void initScheduleThreadPool(ScheduledExecutorService scheduleThreadPool) {
        this.scheduleThreadPool = scheduleThreadPool;
    }

    /**
     * 创建对象
     *
     * @param clasz 对象Class
     * @return 实例
     */
    Object createObject(Class clasz) {
        try {
            return ReflectUtils.newInstance(clasz);
        } catch (Exception ex) {
            throw new Error("必须声明一个无参构造方法", ex);
        }
    }

    /**
     * 注册
     *
     * @param obj 消息发送者
     */
    final void regist(Notifier obj) {
        if (obj instanceof Mediator) {
            this.registMediator((Mediator) obj);
        } else if (obj instanceof Proxy) {
            this.registProxy((Proxy) obj);
        }
    }

    /**
     * 注册数据代理
     *
     * @param name 数据代理唯一标识名
     * @param proxy 数据处理集中代理器，当并发高的情况下需手动处理数据同步访问问题
     */
    final void registProxy(String name, Proxy proxy) {
        if (proxy == null) {
            return;
        }
        if (!StringUtils.isNotEmpty(name)) {
            throw new Error("注册的Proxy.name不能为空");
        }
        if (hasProxy(name)) {
            removeProxy(name);
        }
        proxyMap.put(name, proxy);
    }

    /**
     * 注册数据代理，此方式会以别名的方式多次注册同一对象，即注册单例
     *
     * @param <T> 限定类型
     * @param proxyClass 数据处理集中代理器
     * @param names 数据代理的唯一名称，可以为null，为null则将类限定名作为唯一名称
     * @return 数据代理模型对象
     */
    public <T extends Proxy> T registProxy(Class<T> proxyClass, String... names) {
        Proxy _proxy = (Proxy) createObject(proxyClass);
        final Proxy proxy = _proxy;
        String _name1 = proxy.name;
        String _name2 = proxyClass.getTypeName();
        if (_name1 != null) {
            registProxy(_name1, proxy);
            if (!_name1.equals(_name2)) {
                registProxy(_name2, proxy);
                proxy.addAlias(_name2);
            }
            if (names.length > 0) {
                Set<String> set = new HashSet<>(names.length);
                for (String name : names) {
                    if (name != null && !name.equals(_name1) && !name.equals(_name2)) {
                        set.add(name);
                    }
                }
                set.forEach((String name) -> {
                    registProxy(name, proxy);
                    proxy.addAlias(name);
                });
            }
        } else {
            ReflectUtils.setFieldValue(proxy, "name", _name2);
            registProxy(proxy.name, proxy);
            if (names.length > 0) {
                Set<String> set = new HashSet<>(names.length);
                set.addAll(Arrays.asList(names));
                set.forEach((String name) -> {
                    if (name != null && !name.equals(_name2)) {
                        registProxy(name, proxy);
                        proxy.addAlias(name);
                    }
                });
            }
        }
        proxy.facade = this;
        proxy.onRegister();
        return (T) proxy;
    }

    /**
     * 注册数据代理，此方式会以别名的方式多次注册同一对象，即注册单例
     *
     * @param <T> 限定类型
     * @param proxyClass 数据处理集中代理器
     * @return 数据代理模型对象
     */
    final public <T extends Proxy> T registProxy(Class<T> proxyClass) {
        return registProxy(proxyClass, (String) null);
    }

    /**
     * 注册数据代理
     *
     * @param <T> 限定类型
     * @param proxy 数据处理集中代理器，当并发高的情况下需手动处理数据同步访问问题
     * @return 数据处理
     */
    final public <T extends Proxy> T registProxy(Proxy proxy) {
        Proxy _proxy = (Proxy) createObject(proxy.getClass());
        //Proxy _proxy = proxy;
        registProxy(_proxy.name, _proxy);
        _proxy.facade = this;
        _proxy.onRegister();
        return (T) _proxy;
    }

    /**
     * 是否已经注册了名为proxyName的数据代理处理器
     *
     * @param proxyName 数据处理代理器名称，此代理器的唯一标识
     * @return true 存在此代理器
     */
    final public boolean hasProxy(String proxyName) {
        return proxyMap.containsKey(proxyName);
    }

    /**
     * 是否存在相应的数据模型
     *
     * @param proxyClass 模型代理类
     * @return true 存在
     */
    final public boolean hasProxy(Class<? extends Proxy> proxyClass) {
        String _name = proxyClass.getTypeName();
        return hasProxy(_name);
    }

    /**
     * 获取数据代理
     *
     * @param <T> 限定类型
     * @param proxyName 数据处理代理器名称，此代理器的唯一标识
     * @return 数据处理代理器
     */
    final public <T extends Proxy> T retrieveProxy(String proxyName) {
        return (T) proxyMap.get(proxyName);
    }

    /**
     * 获取数据代理
     *
     * @param <T> 限定类型
     * @param proxyClass 数据处理代理器类
     * @return 数据处理代理器
     */
    final public <T extends Proxy> T retrieveProxy(Class<T> proxyClass) {
        return retrieveProxy(proxyClass.getTypeName());
    }

    /**
     * 移除数据代理
     *
     * @param <T> 限定类型
     * @param proxyName 数据代理器唯一标识
     * @return 数据代理
     */
    final public <T extends Proxy> T removeProxy(String proxyName) {
        Proxy proxy = proxyMap.remove(proxyName);
        if (proxy != null) {
            proxy.onRemove();
        }
        return (T) proxy;
    }

    /**
     * 移除数据代理
     *
     * @param <T> 限定类型
     * @param proxy 数据代理器
     * @return 数据代理
     */
    final public <T extends Proxy> T removeProxy(Proxy proxy) {
        if (proxy == null) {
            return null;
        }
        //因为是注册的相同的事件，所以此处注册的别名对应的观察者也一并清除
        String[] alis = proxy.getAliases();
        if (alis != null && alis.length > 0) {
            for (String alisName : alis) {
                proxyMap.remove(alisName);
            }
        }
        return removeProxy(proxy.name);
    }

    /**
     * 移除数据代理
     *
     * @param <T> 限定类型
     * @param proxyClass 数据代理类型
     * @return 数据代理
     */
    final public <T extends Proxy> T removeProxy(Class<T> proxyClass) {
        Proxy proxy = retrieveProxy(proxyClass.getTypeName());
        removeProxy(proxy);
        return (T) proxy;
    }

    /**
     * 注册事件观察者
     *
     * @param name 事件观察者名称
     * @param observer 事件观察者
     */
    final void registMediator(String name, Mediator observer) {
        if (observer == null) {
            return;
        }
        if (!StringUtils.isNotEmpty(name)) {
            throw new Error("注册的Observer.name不能为空");
        }
        if (hasMediator(name)) {
            removeMediator(name);
        }
        mediaMap.put(name, observer);
    }

    /**
     * 注册事件观察者感兴趣的事件
     *
     * @param observer 事件观察者
     */
    final void registMediatorMessageInterests(Mediator observer) {
        Object[] notiIds = observer.listMessageInterests();
        if (notiIds == null || notiIds.length == 0) {
            return;
        }
        for (Object notiId : notiIds) {
            List<Mediator> list = notiMediMap.get(notiId);
            if (list == null) {
                list = new Stack<>();
                notiMediMap.put(notiId, list);
            }
            if (list.contains(observer) == false) {
                list.add(observer);
            }
        }
        observer.facade = this;
        observer.onRegister();
    }

    /**
     * 注册事件观察者，此方式会以别名的方式多次注册同一对象，即注册单例
     * <p>
     * 事件观察者能集中监听通知消息，可理解为可处理多个Command的对象
     *
     * @param <T> 限定类型
     * @param observerClass 事件观察者
     * @param names 事件观察者的唯一名称，可以为null，为null则将类限定名作为唯一名称
     * @return 事件观察者对象
     */
    <T extends Mediator> T registMediator(Class<T> observerClass, String... names) {
        Mediator _observer = (Mediator) createObject(observerClass);
        final Mediator observer = _observer;
        String _name1 = observer.name;
        String _name2 = observerClass.getTypeName();
        if (_name1 != null) {
            registMediator(_name1, observer);
            if (!_name1.equals(_name2)) {
                registMediator(_name2, observer);
                observer.addAlias(_name2);
            }
            if (names.length > 0) {
                Set<String> set = new HashSet<>(names.length);
                for (String name : names) {
                    if (name != null && !name.equals(_name1) && !name.equals(_name2)) {
                        set.add(name);
                    }
                }
                set.forEach((String name) -> {
                    registMediator(name, observer);
                    observer.addAlias(name);
                });
            }
        } else {
            ReflectUtils.setFieldValue(observer, "name", _name2);
            registMediator(observer.name, observer);
            if (names.length > 0) {
                Set<String> set = new HashSet<>(names.length);
                set.addAll(Arrays.asList(names));
                set.forEach((String name) -> {
                    if (name != null && !name.equals(_name2)) {
                        registMediator(name, observer);
                        observer.addAlias(name);
                    }
                });
            }
        }
        registMediatorMessageInterests(observer);
        return (T) observer;
    }

    /**
     * 注册事件观察者
     *
     * @param <T> 限定类型
     * @param observerClass 事件观察者，能集中监听通知消息，可理解为可处理多个Command的对象
     * @return 事件观察者
     */
    final public <T extends Mediator> T registMediator(Class<T> observerClass) {
        return registMediator(observerClass, (String) null);
    }

    /**
     * 注册事件观察者
     *
     * @param <T> 限定类型
     * @param observer 视图代理，能集中监听通知消息，可理解为可处理多个Command的对象
     * @return 事件观察者
     */
    final <T extends Mediator> T registMediator(Mediator observer) {
        registMediator(observer.name, observer);
        registMediatorMessageInterests(observer);
        return (T) observer;
    }

    /**
     * 是否已经注册了名为observerName的事件观察者
     *
     * @param observerName 视图代理处理唯一标识
     * @return true 存在
     */
    final public boolean hasMediator(String observerName) {
        return mediaMap.containsKey(observerName);
    }

    /**
     * 是否存在相应的事件观察者
     *
     * @param observerClass 事件观察者类
     * @return true 存在
     */
    final public boolean hasMediator(Class<? extends Mediator> observerClass) {
        return mediaMap.containsKey(observerClass.getTypeName());
    }

    /**
     * 移除事件观察者
     *
     * @param observerName 事件观察者处理唯一标识
     */
    final <T extends Mediator> T removeMediator(String observerName) {
        Mediator observer = mediaMap.remove(observerName);
        if (observer != null) {
            Object[] notiIds = observer.listMessageInterests();
            if (notiIds == null) {
                return (T) observer;
            }
            for (Object notiId : notiIds) {
                List<Mediator> list = notiMediMap.get(notiId);
                if (list != null) {
                    list.remove(observer);
                    if (list.isEmpty()) {
                        notiMediMap.remove(notiId);
                    }
                }
            }
            observer.facade = null;
            observer.onRemove();
        }
        return (T) observer;
    }

    /**
     * 移除事件观察者
     *
     * @param <T> 限定类型
     * @param observer 事件观察者
     * @return 事件观察者
     */
    final <T extends Mediator> T removeMediator(Mediator observer) {
        if (observer == null) {
            return null;
        }
        //因为是注册的相同的事件，所以此处注册的别名对应的观察者也一并清除
        String[] alis = observer.getAliases();
        if (alis != null && alis.length > 0) {
            for (String alisName : alis) {
                mediaMap.remove(alisName);
            }
        }
        return removeMediator(observer.name);
    }

    /**
     * 移除事件观察者
     *
     * @param <T> 限定类型
     * @param observerClass 事件观察者类型
     * @return 事件观察者
     */
    final public <T extends Mediator> T removeMediator(Class<T> observerClass) {
        Mediator observer = retrieveMediator(observerClass.getTypeName());
        removeMediator(observer);
        return (T) observer;
    }

    /**
     * 获取事件观察者
     *
     * @param <T> 限定类型
     * @param observerName 事件观察者处理唯一标识
     * @return 事件观察者�
     */
    final <T extends Mediator> T retrieveMediator(String observerName) {
        return (T) mediaMap.get(observerName);
    }

    /**
     * 获取数据代理
     *
     * @param <T> 限定类型
     * @param observerClass 事件观察者类型
     * @return 事件观察者
     */
    final public <T extends Mediator> T retrieveMediator(Class<T> observerClass) {
        return retrieveMediator(observerClass.getTypeName());
    }

    /**
     * 获取通知对象，可为数据代理处理器或者为视图代理器
     *
     * @param name 通知对象的唯一标识
     * @return 通知对象
     * @see Proxy
     * @see Mediator
     */
    final Notifier retrieve(String name) {
        if (proxyMap.containsKey(name)) {
            return proxyMap.get(name);
        }
        return mediaMap.get(name);
    }

    /**
     * 是否存在命令执行器
     *
     * @param cmd 命令执行器唯一标识
     * @return true 存在
     * @see Command
     */
    final public boolean hasCommand(Object cmd) {
        return cmdPoolMap.containsKey(cmd);
    }

    /**
     * 注册命令执行器
     *
     * @param cmd 命令执行器唯一标识
     * @param clasz 命令执行器类对象
     * @param pooledSize 池化大小
     */
    final public void registCommand(Object cmd, Class<? extends Command> clasz, int pooledSize) {
        if (cmdPoolMap.containsKey(cmd)) {
            cmdPoolMap.remove(cmd);
        }
        cmdPoolMap.put(cmd, new CommandPool(this, pooledSize, clasz));
    }

    /**
     * 注册命令执行器，默认不池化
     *
     * @param cmd 命令执行器唯一标识
     * @param clasz 命令执行器类对象
     */
    final public void registCommand(Object cmd, Class<? extends Command> clasz) {
        registCommand(cmd, clasz, -1);
    }

    /**
     * 消息调度处理
     *
     * @param msg 待处理的消息
     * @param asyn true为异步，设为true时须调用initThreadPool方法初始化线程池
     */
    @Override
    public void sendMessage(final Message msg, final boolean asyn) {
        if (msg == null) {
            return;
        }
        if (cmdPoolMap.containsKey(msg.getWhat())) {
            final CommandPool pool = cmdPoolMap.get(msg.getWhat());
            final Command cmd = pool.obtain();
            if (cmd != null) {
                cmd.facade = this;
                cmd.cmd = msg.getWhat();
                if (!asyn) {
                    cmd.execute(msg);
                    pool.idle(cmd);
                } else {
                    if (threadPool == null) {
                        throw new Error(String.format("执行异步sendMessage操作前请先调用 %s.initThreadPool方法初始化线程池", this.getClass().toString()));
                    }
                    threadPool.execute(() -> {
                        cmd.execute(msg);
                        pool.idle(cmd);
                    });
                }
            }
        }
        if (notiMediMap.containsKey(msg.getWhat())) {
            List<Mediator> list = notiMediMap.get(msg.getWhat());
            if (list != null && !list.isEmpty()) {
                list.stream().forEach((observer) -> {
                    if (!asyn) {
                        observer.handleMessage(msg);
                    } else {
                        if (threadPool == null) {
                            throw new Error(String.format("执行异步sendMessage操作前请先调用 %s.initThreadPool方法初始化线程池", this.getClass().toString()));
                        }
                        threadPool.execute(() -> {
                            observer.handleMessage(msg);
                        });
                    }
                });
            }
        }
    }

    /**
     * 同步消息调度处理
     *
     * @param msg 通知消息
     */
    @Override
    final public void sendMessage(Message msg) {
        sendMessage(msg, false);
    }

    /**
     * 终止任务调度
     *
     * @param name 任务名
     */
    void cancelSchedule(String name) {
        if (scheduleFutureMap.containsKey(name)) {
            ScheduledFuture<?> future = scheduleFutureMap.remove(name);
            future.cancel(true);
        }
    }

    /**
     * 定时调度执行事件通知
     *
     * @param msg 事件
     * @param scheduler 定时调度器
     */
    @Override
    public void sendMessage(final Message msg, Scheduler scheduler) {
        if (scheduleThreadPool == null) {
            throw new Error(String.format("执行sendMessage定时调度前请先调用 %s.initScheduleThreadPool方法初始化线程池", this.getClass().toString()));
        }
        if (scheduler.facade != null && scheduler.msg != null) {
            throw new Error(String.format("每次调用sendMessage进行事件调度时必须保证%s参数为新的且独立的对象", Scheduler.class.getTypeName()));
        }
        Object what = msg.what;
        if(what instanceof Number) {
            scheduler.name += "_$#_numeric_" + what;
        } else {
            scheduler.name += "_$#_string_" + what;
        }
        scheduler.facade = this;
        scheduler.msg = msg;
        long initDelay;
        long currMillSecs = System.currentTimeMillis();
        if (scheduler.startTime <= 0) {
            scheduler.startTime = currMillSecs;
        }
        scheduler.prevExecTime.set(scheduler.startTime);
        if (scheduler.startTime >= currMillSecs) {
            initDelay = scheduler.startTime - currMillSecs;
        } else {
            initDelay = 0;
        }
        long delay;
        if (scheduler.interval <= 0) {
            delay = 1;
        } else {
            delay = scheduler.interval;
        }
        cancelSchedule(scheduler.name);
        ScheduledFuture<?> future = scheduleThreadPool.scheduleWithFixedDelay(scheduler, initDelay, delay, TimeUnit.MILLISECONDS);
        scheduleFutureMap.put(scheduler.name, future);
    }

    /**
     * 取消调度
     *
     * @param cmd 事件名
     * @param group 调度器组名
     */
    @Override
    public void cancelSchedule(Object cmd, String group) {
        String name = group;
        String cmdStr = cmd.toString();
        if (StringUtils.isNumeric(cmdStr)) {
            name += "_$#_numeric_" + cmdStr;
        } else {
            name += "_$#_string_" + cmdStr;
        }
        cancelSchedule(name);
    }

    /**
     * 释放内存
     */
    final public void dispose() {
        Iterator<Object> ks = notiMediMap.keySet().iterator();
        while (ks.hasNext()) {
            Object key = ks.next();
            List<Mediator> list = notiMediMap.remove(key);
            if (list != null) {
                list.clear();
            }
        }
        ks = cmdPoolMap.keySet().iterator();
        while (ks.hasNext()) {
            Object key = ks.next();
            CommandPool cmdPool = cmdPoolMap.get(key);
            if (cmdPool != null) {
                cmdPool.clear();
            }
        }
        Iterator<String> keys = mediaMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Mediator observer = mediaMap.remove(key);
            if (observer != null) {
                observer.onRemove();
            }
        }
        keys = proxyMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Proxy proxy = proxyMap.remove(key);
            if (proxy != null) {
                proxy.onRemove();
            }
        }
        notiMediMap.clear();
        cmdPoolMap.clear();
        mediaMap.clear();
        proxyMap.clear();
        this.threadPool = null;
        keys = scheduleFutureMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            this.cancelSchedule(key);
        }
        this.scheduleThreadPool = null;
    }

}
