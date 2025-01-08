package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class DictDataUpdateRequest {
    private Long id;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer orderNum;
    private Integer status;
    private String remark;
} 