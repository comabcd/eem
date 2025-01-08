package com.wwm.eems.model.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    private Long id;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private Long educationId;
    private Long politicalId;
    private Long deptId;
    private Long postId;
    private LocalDate entryDate;
    private Integer status;
    private String remark;
} 