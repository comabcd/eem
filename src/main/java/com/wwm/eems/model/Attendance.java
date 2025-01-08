package com.wwm.eems.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Attendance {
    private Long id;
    private Long employeeId;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer status;  // 1:正常, 2:迟到, 3:早退, 4:缺勤
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
} 