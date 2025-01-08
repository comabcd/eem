package com.wwm.eems.model.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeVO {
    private Long id;
    private String employeeNo;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private Long educationId;
    private String education;
    private Long politicalId;
    private String political;
    private Long deptId;
    private String department;
    private Long postId;
    private String post;
    private LocalDate entryDate;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
} 