package com.wwm.eems.model.vo;

import lombok.Data;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-06
 * @Description: 部门本月考勤统计
 * @Version: 1.0
 */
@Data
public class AttendanceStatsVO {
    private Long normal;      // 正常考勤
    private Long late;        // 迟到
    private Long early;       // 早退
    private Long absent;      // 缺勤
    private Integer leave;       // 请假
}
