package com.http.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Collection;

/**
 * {@link com.fasterxml.jackson.annotation.JsonProperty}
 * <p>为属性取别名的注解</p>
 * {@link com.fasterxml.jackson.annotation.JsonIgnore}
 * <p>忽略方法</p>
 * {@link com.fasterxml.jackson.annotation.JsonAutoDetect}
 * <p>类名注解，配合JsonProperty注解使用忽略方法</p>
 */
public class JsonUtils {

    /**
     * 线程安全，可以全局使用
     */
    private static final ObjectMapper mapper = new ObjectMapper();
//    static {
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        //将属性字段全部转为小写
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
//    }

    /**
     * 将java对象转换为json对象
     * @param javaObject java对象
     * @param <T> json对象的限定类型
     * @return json对象
     */
    public final static <T extends JsonNode> T toJsonObject(Object javaObject) {
        if(javaObject instanceof JsonNode) {
            return (T) javaObject;
        }
        //return mapper.convertValue(javaBean, JsonNode.class);
        JsonNode jsonObject = mapper.valueToTree(javaObject);
        return (T) jsonObject;
    }

    /**
     * 判断字符串是否为一个有效的json格式
     * @param jsonInString
     * @return
     */
    public final static boolean isValidJson(String jsonInString ) {
        try {
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 创建一个空的JsonObject对象
     * @return
     */
    public final static ObjectNode createJsonObject() {
        return mapper.createObjectNode();
    }

    /**
     * 创建一个空的JsonArray对象
     * @return
     */
    public final static ArrayNode createJsonArray() {
        return mapper.createArrayNode();
    }

    /**
     * 将java对象转换为json字符串
     * @param value
     * @return
     */
    public final static String toJsonString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 将json字符串转换为json对象
     * @param json
     * @param <T>
     * @return
     */
    public final static <T extends JsonNode> T toJson(String json) {
        if(json == null || "".equals(json)) return null;
        try {
            return (T) mapper.readTree(json);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将json字符串转换为java对象
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public final static <T>T toObject(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取集合类型描述
     * @param collectionClass 集合类型
     * @param elementClasses 集合的元素类型
     * @return 类型描述
     */
    public final static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 将json字符串转换为java集合
     * @param json json字符串
     * @param collectionClass 集合类型
     * @param elementClasses 集合的元素类型
     * @param <T> 转换后的集合限定类型
     * @return 集合对象
     */
    public final static <T> Collection<T> toCollection(String json, Class<?> collectionClass, Class<T> elementClasses) {
        JavaType javaType = getCollectionType(collectionClass, elementClasses);
        try {
            return (Collection<T>) mapper.readValue(json, javaType);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将json字符串转换为java对象
     * @param json json字符串
     * @param type 目标对象类型
     * @param elementClasses 如果目标对象类型为集合，则此参数表示集合的元素类型
     * @return 对象
     */
    public final static Object toObject(String json, Class<?> type, Class<?>... elementClasses) {
        try {
            if(elementClasses.length > 0) {
                JavaType javaType = getCollectionType(type, elementClasses);
                return mapper.readValue(json, javaType);
            }
            return mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
