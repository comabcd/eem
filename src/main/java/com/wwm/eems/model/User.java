package com.wwm.eems.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String realName;
    private String password;
    private Integer role;  // 1:系统管理员, 2:部门管理员, 3:人力资源管理员, 4:普通员工
    private Long deptId;   // 所属部门ID
    private Integer status;  // 1:启用, 0:禁用
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
} 