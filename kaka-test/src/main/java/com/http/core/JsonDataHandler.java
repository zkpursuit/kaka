package com.http.core;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kaka.notice.Command;
import com.kaka.notice.Message;
import org.slf4j.LoggerFactory;

/**
 * json数据处理器
 *
 * @author zhoukai
 */
abstract public class JsonDataHandler extends Command {

    private Logger logger;
    protected final String opcode; //一定要在构造方法里赋值，不然反射赋值不会成功

    public JsonDataHandler() {
        this.opcode = "";
    }

    @Override
    public void execute(Message msg) {
        com.kaka.util.ReflectUtils.setFieldValue(this, "opcode", this.cmd());
        ObjectNode netData = (ObjectNode) msg.getBody();
        HttpJsonRespWriter out = null;
        if (msg instanceof JsonMessage) {
            JsonMessage nm = (JsonMessage) msg;
            out = nm.out;
        }
        try {
            execute(netData, out);
        } catch (Exception ex) {
            getLogger().error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * 数据包具体解析执行，业务逻辑处理
     *
     * @param requestJson
     * @param out
     */
    abstract public void execute(ObjectNode requestJson, HttpJsonRespWriter out);


    protected Logger getLogger() {
        if (logger == null) {
            logger = (Logger) LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

}
