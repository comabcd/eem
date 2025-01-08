package com.wwm.eems.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-07
 * @Description:
 * @Version: 1.0
 */
@Data
public class PostVO {
    private Long id;
    private Long deptId;
    private String deptName;
    private String postCode;
    private String postName;
    private Integer orderNum;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
}
