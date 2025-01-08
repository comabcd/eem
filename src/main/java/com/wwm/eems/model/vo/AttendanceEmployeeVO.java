package com.wwm.eems.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-03
 * @Description:
 * @Version: 1.0
 */
@Data
public class AttendanceEmployeeVO {
    private String date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer status;
    private String remark;
}
