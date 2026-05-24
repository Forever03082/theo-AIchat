package com.theo.aiknowledgebase.dto;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;     // 状态码：200 成功，400 参数错误，401 认证失败，500 服务器错误
    private Boolean success;  // 是否成功
    private String message;   // 提示信息
    private T data;           // 返回的数据

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}