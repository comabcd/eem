package com.wwm.eems.model.request;

import lombok.Data;

@Data
public class LeaveApproveRequest {
    private Long id;
    private Integer status;  // 2:通过, 3:驳回
    private String approveRemark;
} 