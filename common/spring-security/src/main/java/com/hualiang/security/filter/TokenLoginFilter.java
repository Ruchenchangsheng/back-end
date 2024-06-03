package com.hualiang.security.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hualiang.security.custom.NewUserDetails;
import com.hualiang.common.result.Result;
import com.hualiang.common.utils.JWTUtils;
import com.hualiang.common.utils.ResponseUtil;
import com.hualiang.common.result.ResultCodeEnum;
import com.hualiang.model.User;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private StringRedisTemplate redisTemplate;
    // private LoginLogService loginLogService;

    // 构造
    public TokenLoginFilter(AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate) {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/user/login", "POST"));
        this.redisTemplate = redisTemplate;
        // this.loginLogService = loginLogService;
    }

    // 获取用户名和密码认证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 认证成功
    @SuppressWarnings("null")
    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
//        NewUserDetails customUser = (NewUserDetails) auth.getPrincipal();
//        User userInfo = customUser.getUserInfo();
//        request.getSession().setAttribute("user", userInfo);
//        String token = JWTUtils.createToken(userInfo.getUid(), userInfo.getUsername(), JSON.toJSONString(userInfo));
//
//
//        // 添加日志输出
//        logger.info("User " + customUser.getUsername() + " has successfully logged in. Token created: " + token);
//
//
//        Cookie cookie = new Cookie("token", token);
//        Long time = 12 * 60 * 60L;
////        if (request.getParameter("remember").equals("true")) {
////            time = 3 * 24 * 60 * 60L;
////            cookie.setMaxAge(time.intValue());
////        }
//        cookie.setPath("/");
//        response.addCookie(cookie);
//
//        // 保存权限数据
//        redisTemplate.opsForValue().set(customUser.getUsername(), JSON.toJSONString(customUser.getAuthorities()), time, TimeUnit.SECONDS);
//
//        // 记录登录日志
//        // loginLogService.recordLoginLog(customUser.getUsername(), 1,
//        // IpUtil.getIpAddress(request), "登录成功");
//
//        ResponseUtil.out(response, customUser.getResult(token));
//        System.out.println(customUser.getResult(token));
//    }


    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        NewUserDetails customUser = (NewUserDetails) auth.getPrincipal();
        User userInfo = customUser.getUserInfo();
        request.getSession().setAttribute("user", userInfo);
        String token = JWTUtils.createToken(userInfo.getUid(), userInfo.getUsername(), JSON.toJSONString(userInfo));

        // 添加日志输出
        logger.info("User " + customUser.getUsername() + " has successfully logged in. Token created: " + token);

        // 将用户名和 token 存储到 cookie 中
        Cookie cookie = new Cookie("token", token);
        Long time = 12*60*60L;
        cookie.setMaxAge(365*24 * 60 * 60); // 设置 token 的过期时间
        cookie.setPath("/"); // 设置 cookie 的路径
        response.addCookie(cookie);

        // 将用户名和 token 存储到一个对象中
//        Map<String, Object> authResult = new HashMap<>();
//        authResult.put("username", userInfo.getUsername());
//        authResult.put("token", token);

        // 将用户权限信息保存到 Redis 中
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) auth.getAuthorities();
        List<String> authorityStrings = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
        redisTemplate.opsForValue().set(userInfo.getUsername(), JSON.toJSONString(authorityStrings));
        redisTemplate.opsForValue().set(customUser.getUsername(), JSON.toJSONString(customUser.getAuthorities()),time, TimeUnit.SECONDS);


        // 返回认证结果给客户端

        Map<String, Object> authResult = new HashMap<>();
        authResult.put("username", userInfo.getUsername());
        authResult.put("userId", userInfo.getUid());
        authResult.put("token", token);

        Result<Map<String, Object>> result = Result.ok(authResult);
        ResponseUtil.out(response, result);
        System.out.println(result);
        System.out.println(userInfo.getUid());
    }


    // 认证失败
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if (e.getCause() instanceof RuntimeException) {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.DATABASE_ERROR));
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }
}
