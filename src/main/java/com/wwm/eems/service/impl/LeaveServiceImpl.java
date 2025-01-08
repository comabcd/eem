package com.wwm.eems.service.impl;

import com.wwm.eems.mapper.EmployeeMapper;
import com.wwm.eems.mapper.LeaveMapper;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.Leave;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.LeaveAddRequest;
import com.wwm.eems.model.request.LeaveQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.LeaveStatsVO;
import com.wwm.eems.model.vo.LeaveVO;
import com.wwm.eems.service.LeaveService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public void apply(LeaveAddRequest request) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();
        Employee employee = employeeMapper.findByUsername(username);

        // 检查请假时间是否合法
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }

        // 检查是否有重叠的请假记录
        List<Leave> overlaps = leaveMapper.findOverlap(
                employee.getId(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (!overlaps.isEmpty()) {
            throw new RuntimeException("该时间段内已有请假记录");
        }

        // 创建请假记录
        Leave leave = new Leave();
        leave.setEmployeeId(employee.getId());
        leave.setLeaveType(request.getLeaveType());
        leave.setStartTime(request.getStartTime());
        leave.setEndTime(request.getEndTime());
        leave.setDuration((int) Duration.between(request.getStartTime(), request.getEndTime()).toDays());
        leave.setReason(request.getReason());
        leave.setStatus(1); // 待审批

        leave.setUpdateBy(username);
        leave.setCreateTime(LocalDateTime.now());

        leaveMapper.insert(leave);
    }

    @Override
    @Transactional
    public void approve(Long id) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();

        // 检查请假记录是否存在
        Leave leave = leaveMapper.findById(id);
        if (leave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        // 检查状态是否为待审批
        if (leave.getStatus() != 1) {
            throw new RuntimeException("该请假记录已处理");
        }

        // 更新请假记录
        leave.setStatus(3);
        leave.setApproverId(employeeMapper.findByUsername(username).getId());
        leave.setApproveTime(LocalDateTime.now());

        leave.setUpdateBy(u.getUsername());
        leave.setUpdateTime(LocalDateTime.now());

        leaveMapper.update(leave);
    }

    @Override
    @Transactional
    public void reject(Long id, String approveRemark) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();

        // 检查请假记录是否存在
        Leave leave = leaveMapper.findById(id);
        if (leave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        // 检查状态是否为待审批
        if (leave.getStatus() != 1) {
            throw new RuntimeException("该请假记录已处理");
        }

        // 更新请假记录
        leave.setStatus(2);
        leave.setApproverId(employeeMapper.findByUsername(username).getId());
        leave.setApproveTime(LocalDateTime.now());
        leave.setApproveRemark(approveRemark);
        leave.setUpdateBy(u.getUsername());
        leave.setUpdateTime(LocalDateTime.now());

        leaveMapper.update(leave);
    }

    @Override
    @Transactional
    public void cancel(Long id){

        // 检查请假记录是否存在
        Leave leave = leaveMapper.findById(id);
        if (leave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        // 检查状态是否为待审批
        if (leave.getStatus() != 1) {
            throw new RuntimeException("该请假记录不能撤销");
        }

        leaveMapper.delete(id);

        if (leaveMapper.findById(id) != null) {
            throw new RuntimeException("撤销失败");
        }
    }

    @Override
    @Transactional
    public void complete(Long id) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();

        // 检查请假记录是否存在
        Leave leave = leaveMapper.findById(id);
        if (leave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        // 检查状态是否为待销假
        if (leave.getStatus() != 3) {
            throw new RuntimeException("该请假记录不能销假");
        }

        leave.setStatus(4);
        leave.setReturnTime(LocalDateTime.now());
        leave.setUpdateBy(username);
        leave.setUpdateTime(LocalDateTime.now());

        leaveMapper.update(leave);
    }

    @Override
    public Integer getCurrentMonthLeaveDays() {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();

        Long employeeId = employeeMapper.findByUsername(username).getId();

        return leaveMapper.getCurrentMonthLeaveDays(employeeId);
    }

    @Override
    public List<Map<String, Object>> getLeaveTypeCount() {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();

        Long employeeId = employeeMapper.findByUsername(username).getId();

        return leaveMapper.getLeaveTypeCount(employeeId);
    }

    @Override
    public LeaveStatsVO getMonthlyLeaveStats() {
        // 获取当前用户的部门ID
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        Employee e = employeeMapper.findByUsername(u.getUsername());
        Long deptId = e.getDeptId();

        // 获取本月的每日请假统计
        List<Map<String, Object>> dailyStats = leaveMapper.getDailyLeaveStats(deptId);

        // 构建返回数据
        LeaveStatsVO stats = new LeaveStatsVO();

        // 处理统计数据
        for (Map<String, Object> daily : dailyStats) {
            stats.getDates().add(daily.get("leaveDate").toString());
            stats.getCounts().add(((Number) daily.get("count")).longValue());
        }

        return stats;
    }


    @Override
    public Leave findById(Long id) {
        return leaveMapper.findById(id);
    }

    @Override
    public PageResult<LeaveVO> findByCondition(LeaveQueryRequest request) {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        if (u.getRole() == 2) {
            request.setDeptId(employeeMapper.findByUsername(u.getUsername()).getDeptId());
        }

        // 查询总记录数
        long total = leaveMapper.countByCondition(request);

        // 查询数据列表
        List<LeaveVO> list = leaveMapper.findByCondition(request);

        return PageResult.build(list, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<LeaveVO> findMyLeaves(LeaveQueryRequest request) {
        // 获取当前用户
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        String username = u.getUsername();
        Employee employee = employeeMapper.findByUsername(username);

        // 设置查询条件
        request.setEmployeeId(employee.getId());

        return leaveMapper.findByCondition(request);
    }

} 