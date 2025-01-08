package com.wwm.eems.service;

public interface TokenBlacklistService {
    /**
     * 将token加入黑名单
     */
    void addToBlacklist(String token);

    /**
     * 检查token是否在黑名单中
     */
    boolean isBlacklisted(String token);
} 