package com.wwm.eems.model.request;

import lombok.Data;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:
 * @Version: 1.0
 */
@Data
public class DictDataRequest {
    private String dictType;
    private String dictName;
    private Integer status;
}
