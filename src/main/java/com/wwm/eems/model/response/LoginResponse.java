package com.wwm.eems.model.response;

import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String username;
    private Integer role;
    private String token;
} 