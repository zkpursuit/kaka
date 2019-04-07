package com.http.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.http.constant.OpCode;
import com.kaka.notice.Proxy;

/**
 * 通信协议过滤器组，此处仅演示单一的过滤，多重过滤可以定义一个过滤器接口，
 * 然后将过滤器接口的实现类加入到本对象，接着在doFilter中迭代所有过滤器实
 * 现多重过滤，也可自行用责任链模式实现多重过滤。
 */
public class JsonFilterGroup extends Proxy {

    /**
     * 待忽略的协议号，即此处定义的所有协议将不会被过滤，直接放行
     */
    private static final String ignoreCmds[] = new String[]{
            OpCode.cmd_my_first
    };

    public void doFilter(String cmd, ObjectNode jsonObj, HttpJsonRespWriter writer)  throws Throwable {
        boolean flag = false;
        for (String _cmd : ignoreCmds) {
            if (_cmd.equals(cmd)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            this.sendMessage(new JsonMessage(cmd, jsonObj, writer));
            return;
        }
        //其它协议相关数据的统一过滤，包括某些前置数据验证等
    }

}
