package com.wwm.eems.security;

import com.wwm.eems.service.TokenBlacklistService;
import com.wwm.eems.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Component
public class JwtFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private DefaultWebSecurityManager securityManager;

    private boolean isLoginAttempt(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer ");
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    private boolean executeLogin(HttpServletRequest request, HttpServletResponse response) {
        String token = getToken(request);
        
        if (token == null) {
            logger.warn("Token is null");
            return false;
        }

        // 检查token是否在黑名单中
        if (tokenBlacklistService.isBlacklisted(token)) {
            logger.warn("Token is blacklisted");
            return false;
        }

        try {
            // 验证 token
            if (!JwtUtils.verify(token)) {
                logger.warn("Token verification failed");
                return false;
            }

            // 创建 token 对象
            JwtToken jwtToken = new JwtToken(token);
            
            // 绑定SecurityManager到当前线程
            ThreadContext.bind(securityManager);
            
            // 创建新的Subject并绑定到当前线程
            Subject subject = new WebSubject.Builder(securityManager, request, response).buildSubject();
            ThreadContext.bind(subject);
            
            // 登录
            subject.login(jwtToken);
            
            // 验证成功，记录日志
            String username = JwtUtils.getUsername(token);
            logger.info("Token verification successful for user: {}", username);
            logger.info("Current principal: {}", subject.getPrincipal());
            
            return true;
        } catch (Exception e) {
            logger.error("Token verification error", e);
            return false;
        }
    }

    private void responseError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");
        String jsonResponse = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        response.getWriter().write(jsonResponse);
        logger.warn("Authentication failed: {}", message);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        logger.debug("Processing request for path: {}", path);

        try {
            // 处理跨域
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");

            // 对于OPTIONS请求直接放行
            if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
                response.setStatus(HttpStatus.OK.value());
                return;
            }

            // 对于登录相关的接口直接放行
            if (path.startsWith("/auth/login") || 
                path.startsWith("/auth/register") || 
                path.startsWith("/unauthorized")) {
                logger.debug("Allowing access to public path: {}", path);
                chain.doFilter(request, response);
                return;
            }

            // 验证token
            if (isLoginAttempt(request)) {
                if (!executeLogin(request, response)) {
                    responseError(response, "Token 无效或已过期");
                    return;
                }
            } else {
                responseError(response, "未提供有效的认证信息");
                return;
            }

            chain.doFilter(request, response);
        } finally {
            // 清理线程上下文
            ThreadContext.remove();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Initializing JwtFilter");
    }

    @Override
    public void destroy() {
        logger.info("Destroying JwtFilter");
        ThreadContext.remove();
    }
} 