package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.UserAddRequest;
import com.wwm.eems.security.JwtToken;
import com.wwm.eems.service.TokenBlacklistService;
import com.wwm.eems.service.UserService;
import com.wwm.eems.utils.JwtUtils;
import com.wwm.eems.common.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:登录相关
 * @Version: 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    //登录
    @PostMapping("/login")
    public Result login(@RequestBody User loginUser) {
        try {
            // 查询用户
            User user = userService.findByUsername(loginUser.getUsername());
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证密码
            if (!PasswordUtils.matches(loginUser.getPassword(), loginUser.getUsername(), user.getPassword())) {
                return Result.error("密码错误");
            }

            // 查询账号状态
            if (user.getStatus()==0){
                return Result.error("账号已被禁用，如有需要请联系部门主管");
            }

            // 生成 token，包含用户名和角色
            String token = JwtUtils.generateToken(user.getUsername(), user.getRole());

            // 创建 JwtToken
            JwtToken jwtToken = new JwtToken(token);
            
            // 获取Subject并登录
            Subject subject = SecurityUtils.getSubject();
            subject.login(jwtToken);

            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("id", user.getId());
            data.put("realName", user.getRealName());

            return Result.success(data);
        } catch (AuthenticationException e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

/*    @PostMapping("/register")
    public Result register(@RequestBody UserAddRequest request) {
        // 检查用户名是否已存在
        if (userService.findByUsername(request.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        // 保存用户
        userService.add(request);
        return Result.success();
    }*/

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        try {
            // 获取当前Subject
            Subject subject = SecurityUtils.getSubject();
            
            // 获取token并加入黑名单
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                tokenBlacklistService.addToBlacklist(token);
            }
            
            // 执行登出
            if (subject != null) {
                subject.logout();
            }
            
            return Result.success("登出成功");
        } catch (Exception e) {
            return Result.error("登出失败：" + e.getMessage());
        }
    }
} 