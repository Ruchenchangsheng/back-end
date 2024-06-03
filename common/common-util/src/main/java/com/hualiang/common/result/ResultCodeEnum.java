package com.hualiang.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    FAILURE(201, "操作失败"),
    CANCEL_OPERATION(202, "取消本次操作"),
    VERIFICATION_CODE_ERROR(203, "验证码发送失败，请稍后重试"),
    DATABASE_ERROR(204, "数据库异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_REQUEST(206, "请求过于频繁，请稍后重试"),
    BAD_REQUEST(210, "参数异常"),
    LOGIN_AUTH(208, "用户未登录"),
    PERMISSION(209, "无权访问"),
    // ACCOUNT_ERROR(214, "登录失败，账号不存在"),
    LOGIN_ERROR(215, "登录失败，账号或密码错误"),
    LOGIN_EXPIRED(216, "登录状态已过期"),
    ACCOUNT_STOP(217, "账号已停用"),
    VERIFICATION_CODE_FAILED(218, "验证码验证失败或已失效，请稍后重试");

    private Integer code;
    private String message;

}