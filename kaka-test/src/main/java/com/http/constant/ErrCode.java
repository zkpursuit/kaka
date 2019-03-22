package com.http.constant;

/**
 * 错误码
 *
 * @author zkpursuit
 */
public enum ErrCode {

    illegal_request(400, "非法数据请求，请先登录"),
    wrong_json_format(401, "错误的json格式"),
    Interface_not_enabled(402, "接口未启用"),
    interface_disenable(500, "接口未启用");

    private final int code;
    private final String desc;

    /**
     * 枚举构造
     *
     * @param code 错误码
     * @param desc 错误描述
     */
    ErrCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return desc;
    }

}
