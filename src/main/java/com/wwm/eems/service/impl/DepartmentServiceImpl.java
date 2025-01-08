package com.wwm.eems.service.impl;

import com.wwm.eems.mapper.AttendanceMapper;
import com.wwm.eems.mapper.DepartmentMapper;
import com.wwm.eems.mapper.EmployeeMapper;
import com.wwm.eems.mapper.LeaveMapper;
import com.wwm.eems.model.Department;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.AttendanceQueryRequest;
import com.wwm.eems.model.request.DeptAddRequest;
import com.wwm.eems.model.request.DeptUpdateRequest;
import com.wwm.eems.model.vo.DeptStatisticsVO;
import com.wwm.eems.model.vo.DeptTreeVO;
import com.wwm.eems.service.DepartmentService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private LeaveMapper leaveMapper;

    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
    }

    @Override
    public Department findById(Long id) {
        return departmentMapper.findById(id);
    }

    @Override
    public List<DeptTreeVO> getTree() {
        List<Department> departments = departmentMapper.findAll();

        // 构建部门树
        Map<Long, List<Department>> parentMap = departments.stream()
                .collect(Collectors.groupingBy(Department::getParentId));

        return buildTree(0L, parentMap);
    }

    private List<DeptTreeVO> buildTree(Long parentId, Map<Long, List<Department>> parentMap) {
        List<DeptTreeVO> tree = new ArrayList<>();

        List<Department> children = parentMap.get(parentId);
        if (children != null) {
            for (Department dept : children) {
                DeptTreeVO node = new DeptTreeVO();
                node.setId(dept.getId());
                node.setLabel(dept.getDeptName());
                node.setChildren(buildTree(dept.getId(), parentMap));
                node.setOrderNum(dept.getOrderNum());
                node.setLeaderName(dept.getLeader());
                node.setLeaderPhone(dept.getPhone());
                node.setCreateTime(dept.getCreateTime());
                node.setStatus(dept.getStatus());
                tree.add(node);
            }
        }

        return tree;
    }

    @Override
    @Transactional
    public void add(DeptAddRequest request) {

        Department department = new Department();
        department.setDeptName(request.getDeptName());
        department.setParentId(request.getParentId());
        department.setOrderNum(request.getOrderNum());
        department.setStatus(1); // 默认启用

        User u = (User) SecurityUtils.getSubject().getPrincipal();
        department.setCreateBy(u.getUsername());
        department.setCreateTime(LocalDateTime.now());
        department.setRemark(request.getRemark());

        departmentMapper.insert(department);
    }

    @Override
    @Transactional
    public void update(DeptUpdateRequest request) {
        // 检查部门是否存在
        Department department = departmentMapper.findById(request.getId());
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }

        // TODO
        // 检查是否将部门设置为自己的子部门
        if (request.getParentId() == request.getId()) {
            throw new RuntimeException("上级部门不能是自己");
        }

        // 检查是否将部门设置为自己的下级部门
        List<Department> children = departmentMapper.findChildren(request.getId());
        if (children.stream().anyMatch(d -> d.getId().equals(request.getParentId()))) {
            throw new RuntimeException("上级部门不能是自己的下级部门");
        }

        department.setDeptName(request.getDeptName());
        department.setParentId(request.getParentId());
        department.setOrderNum(request.getOrderNum());
        department.setStatus(request.getStatus());
        department.setRemark(request.getRemark());

        User u = (User) SecurityUtils.getSubject().getPrincipal();
        department.setUpdateBy(u.getUsername());

        departmentMapper.update(department);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        // 检查部门是否存在
        Department department = departmentMapper.findById(id);
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }

        departmentMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查部门是否存在
        Department department = departmentMapper.findById(id);
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }

        // 检查是否有子部门
        List<Department> children = departmentMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在下级部门，不能删除");
        }

        // 检查是否有员工
        if (!employeeMapper.findByDeptId(id).isEmpty()) {
            throw new RuntimeException("部门下存在员工，不能删除");
        }

        departmentMapper.deleteById(id);
    }

    @Override
    public DeptStatisticsVO getDeptStatistics() {
        // 获取当前用户的部门ID
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        Employee employee = employeeMapper.findByUsername(u.getUsername());
        Long deptId = employee.getDeptId();

        DeptStatisticsVO statistics = new DeptStatisticsVO();

        //部门人数
        statistics.setTotalEmployees((long) employeeMapper.findByDeptId(deptId).size());

        //进入出勤
        AttendanceQueryRequest aRequest = new AttendanceQueryRequest();
        aRequest.setDeptId(deptId);
        aRequest.setDate(String.valueOf(LocalDate.now()));
        statistics.setTodayAttendance(attendanceMapper.countByCondition(aRequest));

        //请假人数
        statistics.setOnLeave(leaveMapper.countCurrentLeave(deptId));

        //异常考勤
        statistics.setAbnormalAttendance(attendanceMapper.countTodayAbnormal(deptId));

        return statistics;
    }
} 