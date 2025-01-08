package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class DictDataAddRequest {
    private String dictType;
    private String dictName;
    private Integer orderNum;
}