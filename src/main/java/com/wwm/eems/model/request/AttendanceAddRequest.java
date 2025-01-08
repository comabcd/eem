package com.wwm.eems.model.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceAddRequest {
    private LocalDateTime checkTime;
    private String remark;
} 