package com.wwm.eems.common;

import org.apache.shiro.crypto.hash.SimpleHash;

public class PasswordUtils {
    
    private static final String ALGORITHM_NAME = "md5";
    private static final int HASH_ITERATIONS = 2;

    /**
     * 加密密码
     */
    public static String encrypt(String password, String salt) {
        return new SimpleHash(ALGORITHM_NAME, password, salt, HASH_ITERATIONS).toHex();
    }

    /**
     * 验证密码
     */
    public static boolean matches(String password, String salt, String encrypted) {
        return encrypt(password, salt).equals(encrypted);
    }
} 