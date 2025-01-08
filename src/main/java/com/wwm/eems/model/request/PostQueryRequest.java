package com.wwm.eems.model.request;

import lombok.Data;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-06
 * @Description:
 * @Version: 1.0
 */
@Data
public class PostQueryRequest extends PageRequest{
    private Long deptId;
}
