package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.mapper.EmployeeMapper;
import com.wwm.eems.mapper.LeaveMapper;
import com.wwm.eems.mapper.UserMapper;
import com.wwm.eems.model.Attendance;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.AttendanceAddRequest;
import com.wwm.eems.model.request.AttendanceEmployeeQueryRequest;
import com.wwm.eems.model.request.AttendanceQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.AttendanceEmployeeVO;
import com.wwm.eems.model.vo.AttendanceStatsVO;
import com.wwm.eems.model.vo.AttendanceVO;
import com.wwm.eems.service.AttendanceService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-03
 * @Description:考勤相关
 * @Version: 1.0
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeMapper employeeMapper;

    //上班打卡
    @PostMapping("/checkIn")
    public Result<?> checkIn(@RequestBody AttendanceAddRequest request) {

        attendanceService.checkIn(request);
        return Result.success();
    }

    //下班打卡
    @PostMapping("/checkOut")
    public Result<?> checkOut(@RequestBody AttendanceAddRequest request) {
        attendanceService.checkOut(request);
        return Result.success();
    }

    //详情
    @GetMapping("/{id}")
    public Result<Attendance> getById(@PathVariable Long id) {
        Attendance attendance = attendanceService.findById(id);
        return Result.success(attendance);
    }

    //查询列表
    @GetMapping("/search")
    public Result<PageResult<AttendanceVO>> search(AttendanceQueryRequest request) {
        PageResult<AttendanceVO> pageResult = attendanceService.findByCondition(request);
        return Result.success(pageResult);
    }

    //获取员工自己考勤列表
    @GetMapping("/my")
    public Result<List<AttendanceEmployeeVO>> getMyAttendance(AttendanceEmployeeQueryRequest request) {
        List<AttendanceEmployeeVO> list = attendanceService.findMyAttendance(request);
        return Result.success(list);
    }

    //获取员工当日考勤记录
    @GetMapping("/today")
    public Result<Attendance> getTodayRecord() {
        Attendance attendance = attendanceService.findTodayRecord();
        return Result.success(attendance);
    }

    //获取部门本月考勤记录
    @GetMapping("/stats")
    public Result<AttendanceStatsVO> getAttendanceStats() {
        AttendanceStatsVO stats = attendanceService.getMonthlyAttendanceStats();
        return Result.success(stats);
    }

    // 获取月度考勤数据
    @GetMapping("/monthly")
    public Result monthly() {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        Long id = employeeMapper.findByUsername(u.getUsername()).getId();

        // 获取当前月份的开始和结束日期
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        return Result.success(attendanceService.getMonthlyAttendance(id, firstDay, lastDay));
    }
} 