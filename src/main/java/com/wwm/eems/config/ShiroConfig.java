package com.wwm.eems.config;

import com.wwm.eems.security.JwtFilter;
import com.wwm.eems.security.UserRealm;
import jakarta.servlet.Filter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        
        // 设置Realm
        securityManager.setRealm(userRealm);

        // 关闭 ShiroDAO 功能
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        // 不需要将 Shiro Session 中的东西存到任何地方（包括 Http Session 中）
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        // 设置SecurityManager到SecurityUtils，方便全局使用
        SecurityUtils.setSecurityManager(securityManager);

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager, JwtFilter jwtFilter) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        
        // 设置 SecurityManager
        factoryBean.setSecurityManager(securityManager);

        // 添加自定义过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", jwtFilter);
        factoryBean.setFilters(filterMap);

        // 设置无权限时跳转的 url
        factoryBean.setUnauthorizedUrl("/unauthorized");

        // 设置过滤器链
        Map<String, String> filterRuleMap = new LinkedHashMap<>();
        // 设置不需要拦截的路径
        filterRuleMap.put("/auth/login", "anon");
        filterRuleMap.put("/auth/register", "anon");
        filterRuleMap.put("/unauthorized", "anon");

        // 所有请求通过我们自己的 JWT Filter
        filterRuleMap.put("/**", "jwt");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);

        return factoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}