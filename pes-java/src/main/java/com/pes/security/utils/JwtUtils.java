package com.pes.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 提供 Token 的生成、解析、校验等操作，使用 HMAC-SHA256 签名
 */
@Component
public class JwtUtils {

    /** JWT 密钥，从配置文件中读取 */
    @Value("${jwt.secret}")
    private String secret;

    /** JWT 过期时间（秒），从配置文件中读取 */
    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 获取签名密钥，对原始密钥做 SHA-256 哈希后生成 HMAC 密钥
     *
     * @return SecretKey 签名密钥
     */
    private SecretKey getSigningKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 生成 JWT Token
     *
     * @param username 用户名（作为 subject）
     * @param claims   自定义声明（可放入用户 ID、角色等信息）
     * @return JWT Token 字符串
     */
    public String generateToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 JWT Token，获取 Claims
     *
     * @param token JWT Token 字符串
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 判断 Token 是否已过期
     *
     * @param token JWT Token 字符串
     * @return true 表示已过期或无效，false 表示未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从 Token 中获取用户名（subject）
     *
     * @param token JWT Token 字符串
     * @return 用户名，解析失败则返回 null
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}