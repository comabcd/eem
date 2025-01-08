package com.wwm.eems.security;

import com.wwm.eems.model.User;
import com.wwm.eems.service.UserService;
import com.wwm.eems.utils.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 获取用户信息
        User user = (User) principals.getPrimaryPrincipal();
        
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        
        // 添加角色
        Set<String> roles = new HashSet<>();
        roles.add(String.valueOf(user.getRole()));
        info.setRoles(roles);

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        
        // 验证 token
        if (!JwtUtils.verify(token)) {
            throw new AuthenticationException("token无效或已过期");
        }
        
        // 获取用户信息
        String username = JwtUtils.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token无效");
        }
        
        // 从数据库获取用户信息
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        // 验证token中的角色是否匹配
        Integer tokenRole = JwtUtils.getRole(token);
        if (!user.getRole().equals(tokenRole)) {
            throw new AuthenticationException("用户角色不匹配");
        }

        // 创建AuthenticationInfo，使用user对象作为Principal
        return new SimpleAuthenticationInfo(user, token, getName());
    }
} 