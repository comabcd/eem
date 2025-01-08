package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class PostUpdateRequest {
    private Long id;
    private String postCode;
    private String postName;
    private Integer orderNum;
    private Integer status;
    private String remark;
} 