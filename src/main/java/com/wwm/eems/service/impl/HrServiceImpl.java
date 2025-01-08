package com.wwm.eems.service.impl;

import com.wwm.eems.common.PasswordUtils;
import com.wwm.eems.mapper.*;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.EmployeeQueryRequest;
import com.wwm.eems.model.request.OnboardRequest;
import com.wwm.eems.model.vo.DeptDistributionVO;
import com.wwm.eems.model.vo.HrStatisticsVO;
import com.wwm.eems.model.vo.OnboardTrendVO;
import com.wwm.eems.service.HrService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-04
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class HrServiceImpl implements HrService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public void onboard(OnboardRequest request) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(request.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        //新增用户信息
        User user = new User();
        user.setUsername(request.getUsername());
        user.setRealName(request.getName());
        //初始密码123456
        user.setPassword(PasswordUtils.encrypt("123456", request.getUsername()));
        //初始为普通员工
        user.setRole(4);
        user.setStatus(1);

        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        user.setCreateBy(currentUser.getUsername());
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);

        User newUser = userMapper.findByUsername(request.getUsername());
        if (newUser == null) {
            throw new RuntimeException("新增用户信息失败");
        }

        //新增员工信息
        Employee employee = new Employee();
        employee.setUserId(newUser.getId());
        employee.setEmployeeNo(request.getUsername());
        employee.setName(request.getName());
        employee.setGender(request.getGender());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setEducationId(request.getEducationId());
        employee.setPoliticalId(request.getPoliticalId());
        employee.setDeptId(request.getDeptId());
        employee.setPostId(request.getPostId());
        employee.setEntryDate(request.getEntryDate());
        employee.setStatus(1);
        employee.setCreateBy(currentUser.getUsername());
        employee.setCreateTime(LocalDateTime.now());
        employee.setRemark(request.getRemark());

        employeeMapper.insert(employee);

        if (employeeMapper.findByUsername(request.getUsername()) == null) {
            throw new RuntimeException("新增员工信息失败");
        }
    }

    @Override
    public HrStatisticsVO getHrStatistics() {
        HrStatisticsVO statistics = new HrStatisticsVO();

        // 获取总员工数和部门数
        statistics.setTotalEmployees(employeeMapper.countByCondition(new EmployeeQueryRequest()));
        statistics.setTotalDepartments(departmentMapper.getTotalLeafDepartments());

        // 获取本月入职人数
        statistics.setMonthlyOnboard(employeeMapper.getMonthlyOnboard());

        // 计算离职率
        Double turnoverRate = calculateTurnoverRate();
        statistics.setTurnoverRate(turnoverRate);

        return statistics;
    }

    /**
     * 计算离职率
     * 离职率 = (本月离职人数 / 期总人数) * 100%
     */
    private Double calculateTurnoverRate() {
        Long monthlyResign = employeeMapper.getMonthlyResign();
        Long beginningCount = employeeMapper.getMonthBeginningCount();

        if (beginningCount == 0) {
            return 0.0;
        }

        return Double.parseDouble(String.format("%.2f",
                (monthlyResign * 100.0) / beginningCount));
    }

    @Override
    public List<DeptDistributionVO> getDeptDistribution() {
        // 获取所有部门数据
        List<DeptDistributionVO> allDepts = departmentMapper.getDeptDistribution();
        // 构建树形结构
        return buildDeptTree(allDepts);
    }

    @Override
    public OnboardTrendVO getOnboardTrend() {
        // 获取最近30天的入职趋势数据
        List<Map<String, Object>> trendData = employeeMapper.getOnboardTrend();

        OnboardTrendVO trend = new OnboardTrendVO();

        // 生成最近30天的日期列表
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);

        // 创建日期到入职人数的映射
        Map<String, Long> dateCountMap = trendData.stream()
                .collect(Collectors.toMap(
                        item -> item.get("date").toString(),
                        item -> ((Number) item.get("count")).longValue()
                ));

        // 填充数据
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            trend.getDates().add(dateStr);
            trend.getCounts().add(dateCountMap.getOrDefault(dateStr, 0L));
        }

        return trend;
    }

    @Override
    @Transactional
    public void resign(Long id) {//员工id
        Employee e = employeeMapper.findById(id);
        System.out.println(e);
        if (e == null) {
            throw new RuntimeException("该员工不存在");
        }

        User u = userMapper.findById(e.getUserId());
        System.out.println(u);
        if (u == null) {
            throw new RuntimeException("该用户不存在");
        }

        userMapper.deleteById(e.getUserId());

        if (userMapper.findById(u.getId()) != null) {
            throw new RuntimeException("删除失败");
        }

        employeeMapper.deleteById(id);

        if (employeeMapper.findById(id) != null) {
            throw new RuntimeException("删除失败");
        }
    }

    private List<DeptDistributionVO> buildDeptTree(List<DeptDistributionVO> depts) {
        Map<Long, DeptDistributionVO> deptMap = new HashMap<>();
        List<DeptDistributionVO> roots = new ArrayList<>();

        // 构建部门Map
        for (DeptDistributionVO dept : depts) {
            deptMap.put(dept.getId(), dept);
        }

        // 构建树形结构
        for (DeptDistributionVO dept : depts) {
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                roots.add(dept);
            } else {
                DeptDistributionVO parent = deptMap.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                    // 将子部门人数累加到父部门
                    parent.setValue(parent.getValue() + dept.getValue());
                }
            }
        }

        return roots;
    }
}
