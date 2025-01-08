package com.wwm.eems.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtils {
    // token 过期时间，24小时
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    // 密钥
    private static final String SECRET = "eems_secret";

    /**
     * 生成 token
     */
    public static String generateToken(String username, Integer role) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        
        return JWT.create()
                .withClaim("username", username)
                .withClaim("role", role)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 验证 token
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 从 token 中获取用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 token 中获取用户角色
     */
    public static Integer getRole(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("role").asInt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取token的过期时间
     */
    public static long getExpirationTime(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().getTime();
        } catch (Exception e) {
            return 0;
        }
    }
} 