package com.wwm.eems.model.vo;

import lombok.Data;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-06
 * @Description: 部门数据
 * @Version: 1.0
 */
@Data
public class DeptStatisticsVO {
    private Long totalEmployees;      // 部门总人数
    private Long todayAttendance;     // 今日出勤人数
    private Long onLeave;             // 请假人数
    private Long abnormalAttendance;  // 考勤异常人数
}
