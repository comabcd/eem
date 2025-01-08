package com.wwm.eems.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceVO {
    private Long id;
    private String employeeNo;
    private LocalDate attendanceDate;
    private String name;
    private String post;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer status;
    private String statusName;
    private String remark;
} 