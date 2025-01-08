package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String employeeNo;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private Long deptId;
    private Long postId;
} 