package com.wwm.eems.service.impl;

import com.wwm.eems.mapper.AttendanceMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LeaveMapper leaveMapper;

    // 上班时间：9:00
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    // 下班时间：18:00
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);

    @Override
    @Transactional
    public void checkIn(AttendanceAddRequest request) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();
        Employee employee = employeeMapper.findByUsername(username);

        // 检查是否已经打卡
        Attendance exist = attendanceMapper.findTodayRecord(employee.getId());
        if (exist != null) {
            throw new RuntimeException("今日已打卡");
        }

        // 创建考勤记录
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employee.getId());
        attendance.setAttendanceDate(LocalDate.from(LocalDateTime.now()));
        attendance.setCheckInTime(LocalDateTime.now());

        // 判断是否迟到
//        LocalTime checkInTime = request.getCheckTime().toLocalTime();
        LocalTime checkInTime = LocalTime.now();

        System.out.println("=============================");
        System.out.println(checkInTime);
        System.out.println(WORK_START_TIME);
        System.out.println(checkInTime.isAfter(WORK_START_TIME));
        attendance.setStatus(checkInTime.isAfter(WORK_START_TIME) ? 2 : 1);

//        attendance.setCreateBy(username);
        attendance.setCreateTime(LocalDateTime.now());
        attendance.setRemark(request.getRemark());

        attendanceMapper.insert(attendance);
    }

    @Override
    @Transactional
    public void checkOut(AttendanceAddRequest request) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();
        Employee employee = employeeMapper.findByUsername(username);

        // 获取今日打卡记录
        Attendance attendance = attendanceMapper.findTodayRecord(employee.getId());
        if (attendance == null) {
            throw new RuntimeException("请先打卡上班");
        }
        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退");
        }

        attendance.setEmployeeId(employee.getId());

        // 更新签退信息
        attendance.setCheckOutTime(LocalDateTime.now());

//        LocalTime checkOutTime = request.getCheckTime().toLocalTime();
        LocalTime checkOutTime = LocalTime.now();


//        attendance.setStatus(checkOutTime.isBefore(WORK_END_TIME) ? attendance.getStatus() == 1 ? 3 : 2 : 0);
        // 判断是否早退
        if (checkOutTime.isBefore(WORK_END_TIME)) {
            //判断是否缺勤(迟到+早退)
            if (attendance.getStatus() == 2) {
                attendance.setStatus(4);
            } else if (attendance.getStatus() == 1) {
                attendance.setStatus(3);
            }
        }

//        attendance.setUpdateBy(username);
        attendance.setRemark(request.getRemark());

        attendanceMapper.update(attendance);
    }

    @Override
    public Attendance findById(Long id) {
        return attendanceMapper.findById(id);
    }

    @Override
    public Attendance findTodayRecord() {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();
        System.out.println(username);
        Employee employee = employeeMapper.findByUsername(username);
        if (employee == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取今日打卡记录
        return attendanceMapper.findTodayRecord(employee.getId());
    }

    @Override
    public PageResult<AttendanceVO> findByCondition(AttendanceQueryRequest request) {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        if (userMapper.findByUsername(u.getUsername()).getRole() == 2) {
            request.setDeptId(employeeMapper.findByUsername(u.getUsername()).getDeptId());
        }

        // 查询总记录数
        long total = attendanceMapper.countByCondition(request);

        // 查询数据列表
        List<AttendanceVO> list = attendanceMapper.findByCondition(request);

        return PageResult.build(list, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<AttendanceEmployeeVO> findMyAttendance(AttendanceEmployeeQueryRequest request) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();

        System.out.println("u = " + u);

        String username = u.getUsername();

        Employee employee = employeeMapper.findByUsername(username);

        // 设置查询条件
        request.setEmployeeId(employee.getId());

        return attendanceMapper.findOwnByCondition(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus()
        );
    }

    @Override
    public AttendanceStatsVO getMonthlyAttendanceStats() {
        // 获取当前用户的部门ID
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        Employee e = employeeMapper.findByUsername(u.getUsername());
        Long deptId = null;
        Long empId = null;
        if (u.getRole() == 2) {
            deptId = e.getDeptId();
        }

        if (u.getRole() == 4) {
            empId = e.getId();
        }

        // 获取本月考勤统计
        return attendanceMapper.getMonthlyStats(deptId, empId);
    }

    // 获取员工月度考勤数据和统计
    public Map<String, Object> getMonthlyAttendance(Long empId, LocalDate startDate, LocalDate endDate) {
        // 获取考勤记录
        List<Attendance> records = attendanceMapper.selectMonthlyAttendance(empId, startDate, endDate);

        // 统计各状态数量
        long normal = records.stream().filter(a -> a.getStatus() == 1).count();
        long late = records.stream().filter(a -> a.getStatus() == 2).count();
        long early = records.stream().filter(a -> a.getStatus() == 3).count();
        long absent = records.stream().filter(a -> a.getStatus() == 4).count();
        long leave = leaveMapper.getCurrentMonthLeaveDays(empId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("normal", normal);
        result.put("late", late);
        result.put("early", early);
        result.put("absent", absent);
        result.put("leave", leave);

        return result;
    }
} 