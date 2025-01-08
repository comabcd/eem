package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private Long id;
    private Integer role;
    private Integer status;
    private String remark;
} 