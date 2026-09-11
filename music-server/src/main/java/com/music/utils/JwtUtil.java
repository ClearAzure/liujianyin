package com.music.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component//加@Component交给IOC容器管理,当项目启动的时候会自动创建bean对象
public class JwtUtil {

    private final SecretKey key;//JWT 签名的密钥
    private final long expiration;//jwt有效时间，单位毫秒

    //构造方法
    public JwtUtil(@Value("${app.jwt-secret}") String secret,
                   @Value("${app.jwt-expiration}") long expiration) {//@Value从 Spring Boot 配置文件中读取
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));//转换成 JWT 库需要的 SecretKey 对象。
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(userId.toString())//标准的 subject（主题）字段
                .claim("username", username)//给 JWT 添加一个自定义 Claim(内部携带的数据)
                .issuedAt(new Date())//Token 的签发时间。
                .expiration(new Date(System.currentTimeMillis() + expiration))//Token过期时间
                .signWith(key)//使用 SecretKey 对 JWT 进行签名。
                .compact();
    }

    public Long getUserIdFromToken(String token) {//从 JWT 中把用户 ID 取出来。
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {//判断 Token 是否有效
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)//用这个 SecretKey 验证 Token 的签名。
                .build()
                .parseSignedClaims(token)//解析这个带签名的 JWT，并验证它。
                .getPayload();
    }
}
