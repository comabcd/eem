package com.wwm.eems.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceQueryRequest extends PageRequest {
    private Long employeeId;
    private String employeeNo;
    private String name;
    private String date;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long deptId;
    private Long postId;
    private Integer status;
} 