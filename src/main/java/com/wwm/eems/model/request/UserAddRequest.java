package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class UserAddRequest {
    private String username;
    private String realName;
    private String password;
    private Integer role;
} 