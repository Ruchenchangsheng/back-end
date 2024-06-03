package com.hualiang.common.utils;

import java.util.Date;

import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 生成JSON Web令牌的工具类
 */
public class JWTUtils {

    // token过期时间
    private static long tokenExpiration = 7*24 * 60 * 60 * 1000; // 一周
    // 加密秘钥
    private static String tokenSignKey = "3F4428472B4B6250655368566D5971337336763979244226452948404D635166";

    // 根据用户id和用户名称生成token字符串
    public static String createToken(Integer uid, String username, String json) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("uid", uid)
                .claim("username", username)
                .claim("json", json)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Claims getClaims(String token) throws ExpiredJwtException {
        if (!StringUtils.hasLength(token)) {
            return null;
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        return claimsJws.getBody();
    }

    
}