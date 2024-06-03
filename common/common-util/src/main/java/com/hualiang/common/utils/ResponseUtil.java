package com.hualiang.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hualiang.common.result.Result;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> void out(HttpServletResponse response, Result<T> result) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");
        try {
            objectMapper.writeValue(response.getWriter(), result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}