package com.wwm.eems.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private Long deptId;
    private String postCode;
    private String postName;
    private Integer orderNum;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
} 