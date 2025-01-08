package com.wwm.eems.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Leave {
    private Long id;
    private Long employeeId;
    private Integer leaveType;  // 1:事假, 2:病假, 3:年假, 4:调休, 5:其他
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private String reason;
    private Integer status;  // 1:待审批, 2:已通过, 3:已驳回
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveRemark;
    private LocalDateTime returnTime;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
} 