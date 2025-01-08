package com.wwm.eems.model.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeAddRequest {
    private String employeeNo;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private Long educationId;
    private Long politicalId;
    private Long deptId;
    private Long postId;
    private LocalDate entryDate;
    private String remark;
    
    // 用户账号信息
    private String username;
    private String password;
} 