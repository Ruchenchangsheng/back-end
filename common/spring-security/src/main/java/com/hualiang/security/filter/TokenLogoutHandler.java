package com.hualiang.security.filter;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.hualiang.common.result.Result;
import com.hualiang.common.utils.ResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenLogoutHandler implements LogoutSuccessHandler {

//    @Override
//    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        Cookie cookie = new Cookie("token", "");
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        String ajax = request.getHeader("X-Requested-With");
//        if (ajax != null && "XMLHttpRequest".equals(ajax)) {
//            ResponseUtil.out(response, Result.ok().message("注销成功"));
//        } else {
//            response.sendRedirect("/");
//        }
//    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Cookie cookie = new Cookie("token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        String ajax = request.getHeader("X-Requested-With");
        if (ajax != null && "XMLHttpRequest".equals(ajax)) {
            ResponseUtil.out(response, Result.ok().message("注销成功"));
        } else {
            response.sendRedirect("/");
        }

        // 返回空的认证结果给客户端
        ResponseUtil.out(response, Result.ok());

        if (authentication != null && authentication.isAuthenticated()) {
            // 用户成功退出登录
            System.out.println("User logged out successfully.");
        } else {
            // 用户未成功退出登录
            System.out.println("Logout failed or user was not authenticated.");
        }
    }
}
