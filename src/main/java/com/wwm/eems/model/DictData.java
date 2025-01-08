package com.wwm.eems.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DictData {
    private Long id;
    private String dictType;
    private String dictName;
    private Integer orderNum;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
} 