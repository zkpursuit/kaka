package com.kaka.util;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.naming.NamingException;

/**
 * 简单RMI服务的发布和调用，实际应用RMI服务可使用ZooKeeper或其它分布式组件
 *
 * @author zhoukai
 */
public final class RMI {

    /**
     * 注册RMI远程访问对象（RMI服务端）
     *
     * @param <T> 实现了Remote接口的实现类，一般做法是定义继承了Remote接口的自定义接口，定义一个此自定义接口的实现类
     * @param host 发布的RMI服务器IP地址或域名
     * @param port 发布的服务器RMI访问端口
     * @param name 供远程访问的服务名
     * @param serviceObject 供远程访问的服务对象
     * @throws MalformedURLException 将服务URL绑定到jni中的命名空中的异常
     * @throws RemoteException RMI访问异常
     */
    public static final <T extends Remote> void publishRemoteService(String host, int port, String name, T serviceObject) throws RemoteException, MalformedURLException {
        //将名称绑定到对象,即向命名空间注册已经实例化的远程服务对象
        String url = String.format("rmi://%s:%d/%s", host, port, name);
        Remote remoteObject = UnicastRemoteObject.exportObject(serviceObject, 0);
        LocateRegistry.createRegistry(port);
        Naming.rebind(url, remoteObject);
        //Registry registry = LocateRegistry.createRegistry(port);
        //registry.rebind(name, remoteObject);
    }

    /**
     * 获取RMI服务对象
     *
     * @param <T> RMI服务对象接口类型
     * @param host 发布RMI服务的服务器IP地址或域名
     * @param port 发布RMI服务的服务器访问端口
     * @param name RMI服务名
     * @return 实现了Remote接口的远程访问接口对象
     * @throws NamingException jni命名空间异常
     * @throws RemoteException 远程访问异常
     * @throws NotBoundException 捆绑查找服务异常
     */
    public static final <T extends Remote> T lookupRemoteService(String host, int port, String name) throws NamingException, RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        T service = (T) registry.lookup(name);
        return service;
    }

    private RMI() {
    }
}
