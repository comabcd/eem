package com.wwm.eems.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveQueryRequest extends PageRequest {
    private Long employeeId;
    private Long deptId;
    private Long postId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer status;
    private Integer leaveType;
} 