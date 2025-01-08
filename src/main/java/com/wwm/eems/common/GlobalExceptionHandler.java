package com.wwm.eems.common;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<?> handleAuthenticationException(AuthenticationException e) {
        logger.error("认证异常", e);
        String message = "认证失败";
        if (e instanceof UnknownAccountException) {
            message = "用户不存在";
        } else if (e instanceof DisabledAccountException) {
            message = "账号已被禁用";
        }
        return Result.error(401, message);
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AuthorizationException.class)
    public Result<?> handleAuthorizationException(AuthorizationException e) {
        logger.error("权限异常", e);
        return Result.error(403, "没有权限");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        logger.error("业务异常", e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        logger.error("系统异常", e);
        return Result.error(500, "系统错误");
    }
} 