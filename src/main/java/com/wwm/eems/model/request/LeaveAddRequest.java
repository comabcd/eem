package com.wwm.eems.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LeaveAddRequest {
    private Integer leaveType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
} 