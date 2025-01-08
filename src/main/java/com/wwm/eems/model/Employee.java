package com.wwm.eems.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private Long userId;
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
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
} 