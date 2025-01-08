package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class DictTypeUpdateRequest {
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
} 