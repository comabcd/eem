package com.wwm.eems.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeQueryRequest extends PageRequest {
    private String employeeNo;
    private String name;
    private Integer gender;
    private Long deptId;
    private Long postId;
    private Integer status;
    private LocalDate entryDateStart;
    private LocalDate entryDateEnd;
}