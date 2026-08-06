package com.pes.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一 API 响应结果封装
 * 用于规范化后端接口的返回格式，所有接口统一返回此对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    /** 状态码，200 表示成功，其他表示失败 */
    private int code;

    /** 提示消息 */
    private String msg;

    /** 响应数据，可为 null */
    private T data;

    private Result() {}

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 返回的数据
     * @param <T>  数据类型
     * @return 包含状态码 200 和数据的 Result 对象
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（不带数据）
     *
     * @param <T> 数据类型
     * @return 包含状态码 200 且 data 为 null 的 Result 对象
     */
    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code 自定义错误状态码
     * @param msg  错误提示消息
     * @param <T>  数据类型
     * @return 包含指定状态码和消息的 Result 对象
     */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败响应（默认 500 状态码）
     *
     * @param msg 错误提示消息
     * @param <T> 数据类型
     * @return 包含状态码 500 和错误消息的 Result 对象
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500, msg, null);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}