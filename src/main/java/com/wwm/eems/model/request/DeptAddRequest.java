package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class DeptAddRequest {
    private String deptName;
    private Long parentId;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private String remark;
} 