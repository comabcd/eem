package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class DeptUpdateRequest {
    private Long id;
    private String deptName;
    private Long parentId;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private Integer status;
    private String remark;
} 