package com.wwm.eems.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-06
 * @Description: 部门分布
 * @Version: 1.0
 */
@Data
public class DeptDistributionVO {
    private Long id;
    private Long parentId;
    private String name;    // 部门名称
    private Long value;          // 人员数量
    private List<DeptDistributionVO> children;  // 子部门分布

    public DeptDistributionVO() {
        this.children = new ArrayList<>();
    }
}
