package com.wwm.eems.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LeaveVO {
    private Long id;
    private String employeeNo;
    private String name;
    private String post;
    private Integer leaveType;
    private String leaveTypeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private String reason;
    private Integer status;
    private String statusName;
    private Long approverId;
    private String approver;
    private LocalDateTime approveTime;
    private String approveRemark;
    private LocalDateTime returnTime;
    private LocalDateTime createTime;
} 