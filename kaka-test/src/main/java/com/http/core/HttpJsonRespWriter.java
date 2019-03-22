package com.http.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.http.util.JsonUtils;

/**
 * 待发送的JSON对象
 *
 * @author zhoukai
 */
public class HttpJsonRespWriter {

    private ArrayNode array; //待发送的json数据
    private StringBuilder strBuilder; //待发送的字符串数据，与array中的数据互斥，array优先级最高
    private boolean forceArray = false; //是否强制以json数组形式发送数据到客户端

    public HttpJsonRespWriter() {

    }

    public void setForceArray(boolean bool) {
        this.forceArray = bool;
    }

    public boolean getForceArray() {
        return forceArray;
    }

    /**
     * 追加JSON对象
     *
     * @param json 待追加的对象
     */
    private void addJsonObject(JsonNode json) {
        if (array == null) {
            array = JsonUtils.createJsonArray();
        }
        if (json instanceof ArrayNode) {
            ArrayNode arr = (ArrayNode) json;
            array.addAll(arr);
        } else {
            array.add(json);
        }
    }

    /**
     * 追加
     *
     * @param obj 追加的对象实例，支持Json对象、HttpResponseWriter对象、字符串
     *            <p>其它未支持的对象全部以字符串表示</p>
     * @return 本实例
     */
    HttpJsonRespWriter writeObject(Object obj) {
        if (obj instanceof JsonNode) {
            addJsonObject((JsonNode) obj);
        } else if(obj instanceof HttpJsonRespWriter) {
            HttpJsonRespWriter writer = (HttpJsonRespWriter) obj;
            ArrayNode arr = writer.array;
            boolean flag = true;
            if(arr != null) {
                int size = arr.size();
                if(size > 0) {
                    for(int i = 0; i < size; i++) {
                        JsonNode node = arr.get(i);
                        writeObject(node);
                    }
                    flag = false;
                }
            }
            if(flag && strBuilder != null && strBuilder.length() > 0) {
                writeObject(strBuilder.toString());
            }
        } else {
            if (strBuilder == null) {
                strBuilder = new StringBuilder();
            }
            strBuilder.append(String.valueOf(obj));
        }
        return this;
    }

    /**
     * 写入待发送的数据
     *
     * @param cmd 协议号
     * @param sendData 等待发送的数据
     */
    public void write(String cmd, Object sendData) {
        ObjectNode sendDataJson = JsonUtils.createJsonObject();
        if(sendData != null) {
            if(sendData instanceof ObjectNode) {
                ObjectNode jsonObj = (ObjectNode) sendData;
                if(jsonObj.has("error") || jsonObj.has("info")) {
                    sendDataJson.setAll(jsonObj);
                } else {
                    if(!jsonObj.has("data")) {
                        sendDataJson.set("data", jsonObj);
                    } else {
                        sendDataJson.setAll(jsonObj);
                    }
                }
            } else if(sendData instanceof ArrayNode) {
                ArrayNode jsonArr = (ArrayNode) sendData;
                sendDataJson.set("data", jsonArr);
            } else {
                sendDataJson.put("data", sendData.toString());
            }
        } else {
            sendDataJson.put("data", "null");
        }
        String cmdStr = "cmd";
        String systime = "systime";
        if (!sendDataJson.has(cmdStr)) {
            sendDataJson.put(cmdStr, cmd);
        }
        if (!sendDataJson.has(systime)) {
            sendDataJson.put(systime, System.currentTimeMillis());
        }
        writeObject(sendDataJson);
    }

    /**
     * 清除
     */
    public void clear() {
        if (array != null) {
            array.removeAll();
        }
        if (strBuilder != null && strBuilder.length() > 0) {
            strBuilder.delete(0, strBuilder.length());
        }
    }

    /**
     * 判断此对象的字符串表示是否为json格式
     *
     * @return true是json格式
     */
    public boolean isJsonString() {
        if (array != null) {
            return true;
        }
        return JsonUtils.isValidJson(strBuilder.toString());
    }

    /**
     * 是否为空
     *
     * @return true为空
     */
    public boolean isEmpty() {
        if (array != null && array.size() > 0) {
            return false;
        }
        return !(strBuilder != null && strBuilder.length() > 0);
    }

    /**
     * 将本对象转换为字符串
     *
     * @return 本对象的字符串表示
     */
    @Override
    public String toString() {
        if (array != null) {
            if (forceArray) {
                return JsonUtils.toJsonString(array);
            }
            if (array.size() == 1) {
                Object obj = array.get(0);
                if (obj instanceof JsonNode) {
                    return JsonUtils.toJsonString(obj);
                }
                return obj.toString();
            }
            return JsonUtils.toJsonString(array);
        }
        if (strBuilder != null) {
            return strBuilder.toString();
        }
        return super.toString();
    }

}
