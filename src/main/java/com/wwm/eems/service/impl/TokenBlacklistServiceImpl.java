package com.wwm.eems.service.impl;

import com.wwm.eems.service.TokenBlacklistService;
import com.wwm.eems.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final ConcurrentMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public TokenBlacklistServiceImpl() {
        // 启动定时任务，每小时清理一次过期的token
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::cleanExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    @Override
    public void addToBlacklist(String token) {
        // 获取token的过期时间
        long expiration = JwtUtils.getExpirationTime(token);
        if (expiration > System.currentTimeMillis()) {
            blacklist.put(token, expiration);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        Long expiration = blacklist.get(token);
        if (expiration == null) {
            return false;
        }
        
        // 如果token已过期，从黑名单中移除
        if (expiration <= System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    private void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
} 