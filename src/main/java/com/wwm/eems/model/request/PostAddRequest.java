package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class PostAddRequest {
    private String postCode;
    private String postName;
    private Long deptId;
    private Integer orderNum;
    private String remark;
} 