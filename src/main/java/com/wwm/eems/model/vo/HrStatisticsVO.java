package com.wwm.eems.model.vo;

import lombok.Data;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-06
 * @Description: hr模块数据统计
 * @Version: 1.0
 */
@Data
public class HrStatisticsVO {
    private Long totalEmployees;      // 总员工数
    private Long totalDepartments;    // 总部门数
    private Long monthlyOnboard;      // 本月入职人数
    private Double turnoverRate;      // 离职率
}
