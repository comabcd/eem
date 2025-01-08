package com.wwm.eems.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Department {
    private Long id;
    private String deptName;
    private Long parentId;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
} 