package com.wwm.eems.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeptTreeVO {
    private Long id;
    private String label;  // 部门名称
    private Integer orderNum;
    private String leaderName;
    private String leaderPhone;
    private Integer status;
    private LocalDateTime createTime;
    private List<DeptTreeVO> children;
}