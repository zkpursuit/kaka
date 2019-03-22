package com.http.business;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.http.constant.OpCode;
import com.http.core.HttpJsonRespWriter;
import com.http.core.JsonDataHandler;
import com.http.util.JsonUtils;
import com.kaka.notice.annotation.Handler;

/**
 * 一个简单的求和
 *
 * @author zhoukai
 */
@Handler(cmd = OpCode.cmd_my_first, type = String.class)
public class MyFirstJsonHandler extends JsonDataHandler {

    /**
     *
     * @param requestJson 客户端发送的json协议请求
     * @param out 返回给客户端的json数据写入器
     */
    @Override
    public void execute(ObjectNode requestJson, HttpJsonRespWriter out) {
        int min = requestJson.get("min").asInt();
        int max = requestJson.get("max").asInt();
        int _min = min;
        min = Math.min(_min, max);
        max = Math.max(max, _min);

        ObjectNode json = JsonUtils.createJsonObject();
        int sum = 0;
        for(int i = min; i <= max; i++) {
            sum += i;
        }
        json.put("min", min);
        json.put("max", max);
        json.put("sum", sum);
        out.write(this.opcode, json);
    }
}
