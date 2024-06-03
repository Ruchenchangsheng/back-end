package com.hualiang.common.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hualiang.common.result.Result;
import com.hualiang.common.result.ResultCodeEnum;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // // 全局异常处理
    // @ExceptionHandler(Exception.class)
    // public String error(Exception e) {
    // e.printStackTrace();
    // return "error";
    // }

    // 特定异常处理
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> paramError(ConstraintViolationException e) throws ConstraintViolationException {
        return Result.build(e.getMessage(), ResultCodeEnum.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, String>> fieldError(MethodArgumentNotValidException e) throws MethodArgumentNotValidException {
        List<FieldError> fieldErrors = e.getFieldErrors();
        Map<String, String> map = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return Result.build(map, ResultCodeEnum.BAD_REQUEST);
    }

}