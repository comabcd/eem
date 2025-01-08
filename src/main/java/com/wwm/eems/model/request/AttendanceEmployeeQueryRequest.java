package com.wwm.eems.model.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-03
 * @Description:
 * @Version: 1.0
 */
@Data
public class AttendanceEmployeeQueryRequest {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
}
