package com.wwm.eems.model.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-04
 * @Description: 入职信息
 * @Version: 1.0
 */
@Data
public class OnboardRequest {
    private String username;//员工编号&工号
    private String name;//姓名
    private Integer gender;//性别
    private String phone;//联系电话
    private String email;//邮箱
    private Long educationId;//学历ID
    private Long politicalId;//政治面貌ID
    private Long deptId;//部门ID
    private Long postId;//岗位ID
    private LocalDate entryDate;//入职日期
    private String remark;//备注
}
