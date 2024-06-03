package com.hualiang.security.filter;

import com.hualiang.common.annotation.RequestLimit;
import com.hualiang.common.result.Result;
import com.hualiang.common.result.ResultCodeEnum;
import com.hualiang.common.utils.IpUtil;
import com.hualiang.common.utils.ResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RequestLimitIntercept implements HandlerInterceptor {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        /**
         * isAssignableFrom() 判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口
         * isAssignableFrom()方法是判断是否为某个类的父类
         * instanceof关键字是判断是否某个类的子类
         */
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            // HandlerMethod 封装方法定义相关的信息,如类,方法,参数等
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 获取方法中是否包含注解
            RequestLimit methodAnnotation = method.getAnnotation(RequestLimit.class);
            // 获取类中是否包含注解，也就是controller 是否有注解
            RequestLimit classAnnotation = method.getDeclaringClass().getAnnotation(RequestLimit.class);
            // 如果方法上有注解就优先选择方法上的参数，否则类上的参数
            RequestLimit requestLimit = methodAnnotation != null ? methodAnnotation : classAnnotation;
            if (requestLimit != null) {
                if (isLimit(request, requestLimit)) {
                    ResponseUtil.out(response, Result.build(null, ResultCodeEnum.REPEAT_REQUEST));
                    return false;
                }
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    // 判断请求是否受限
    public boolean isLimit(HttpServletRequest request, RequestLimit requestLimit) {
        HttpSession session = request.getSession();
        String IP = IpUtil.getIpAddress(request);
        String limitKey = request.getServletPath() + session.getId();

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(limitKey)) {
            Long requestCount = ops.increment(limitKey);
            requestCount = requestCount == null ? 1 : requestCount;
            if (requestCount.intValue() > requestLimit.maxCount()) {
                log.info("[{}] -> [{}]({})[Restricted access]", IP, request.getRequestURI(), requestCount);
                return true;
            } else {
                log.info("[{}] -> [{}]({})", IP, request.getRequestURI(), requestCount);
            }
        } else {
            ops.set(limitKey, "1", requestLimit.second(), TimeUnit.SECONDS);
            log.info("[{}] -> [{}]({})", IP, request.getRequestURI(), 1);
        }
        return false;
    }
}
