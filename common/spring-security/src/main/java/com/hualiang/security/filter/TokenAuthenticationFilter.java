package com.hualiang.security.filter;

import com.alibaba.fastjson.JSON;
import com.hualiang.common.result.Result;
import com.hualiang.common.result.ResultCodeEnum;
import com.hualiang.model.User;
import com.hualiang.common.utils.JWTUtils;
import com.hualiang.common.utils.ResponseUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//认证解析过滤器
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private List<String> url = Arrays.asList("/", "/user/login", "/register", "/user/register", "/user/email", "/error");
    private StringRedisTemplate redisTemplate;

    public TokenAuthenticationFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException {
        String uri = request.getRequestURI();
        // 如果是登录接口，直接放行
        if (uri.startsWith("/static/") || url.contains(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String token = getTokenFromCookie(request);

        if (StringUtils.hasLength(token)) {
            // logger.info("token: authenticated");
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, token);
            if (authentication != null) {
                logger.info(String.format("[%s] --> %s", authentication.getName(), uri));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } else {
                ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_EXPIRED));
            }
        } else {
            logger.info("token: unauthorized");
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_AUTH));
        }
    }

    // 获取token认证信息
    @SuppressWarnings("rawtypes")
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String token) {
        Claims claims;
        try {
            claims = JWTUtils.getClaims(token);
        } catch (ExpiredJwtException e) {
            logger.info("token: expired");
            return null;
        }

        HttpSession session = request.getSession();
        String username = claims.get("username", String.class);
        if (username != null && !username.isEmpty()) {
            if (session.getAttribute("user") == null) {
                session.setAttribute("user", JSON.parseObject(claims.get("json", String.class), User.class));
            }
//            System.out.println("登陆成功：");
            String authString = redisTemplate.opsForValue().get(username);
            if (StringUtils.hasLength(authString)) {
                List<Map> mapList = JSON.parseArray(authString, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList) {
                    authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                }
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            } else {
                return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            }
        }
        return null;
    }

    // 从cookie中获取token
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
