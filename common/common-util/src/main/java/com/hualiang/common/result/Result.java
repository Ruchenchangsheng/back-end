package com.hualiang.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Description 统一返回结果类
 * @param T 返回数据类型
 */
@Schema(name = "统一返回结果类")
@Data
@NoArgsConstructor
public class Result<T> {

    @Schema(description = "状态码")
    private Integer code;
    @Schema(description = "操作信息")
    private String message;
    @Schema(description = "返回的数据")
    private T data;

    public static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T data, Integer code, String message) {
        Result<T> result = build(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> fail() {
        return Result.fail(null);
    }

    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAILURE);
    }

    // 链式编程
    public Result<T> data(T data) {
        this.setData(data);
        return this;
    }

    public Result<T> message(String msg) {
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }
    
    public Result<T> codeEnum(ResultCodeEnum resultCodeEnum) {
        this.setCode(resultCodeEnum.getCode());
        this.setMessage(resultCodeEnum.getMessage());
        return this;
    }

}
