package com.wwm.eems.service.impl;

import com.wwm.eems.mapper.EmployeeMapper;
import com.wwm.eems.mapper.UserMapper;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.EmployeeAddRequest;
import com.wwm.eems.model.request.EmployeeUpdateRequest;
import com.wwm.eems.model.request.EmployeeQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.EmployeeVO;
import com.wwm.eems.service.EmployeeService;
import com.wwm.eems.common.PasswordUtils;
import com.wwm.eems.utils.ExcelUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wwm.eems.annotation.DataScope;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    @Override
    public List<Employee> findByDeptId(Long deptId) {
        return employeeMapper.findByDeptId(deptId);
    }

    @Override
//    @DataScope(deptAlias = "e", userAlias = "e")
    public PageResult<EmployeeVO> findByCondition(EmployeeQueryRequest request) {

        User u = (User) SecurityUtils.getSubject().getPrincipal();
        if (u.getRole() == 2) {
            request.setDeptId(employeeMapper.findByUsername(u.getUsername()).getDeptId());
        }

        // 查询总记录数
        long total = employeeMapper.countByCondition(request);

        // 查询当前页数据
        List<EmployeeVO> list = employeeMapper.findByCondition(request);

        // 构建分页结果
        return PageResult.build(list, total, request.getPageNum(), request.getPageSize());
    }


    @Override
    @Transactional
    public void update(EmployeeUpdateRequest request) {
        // 检查员工是否存在
        Employee employee = employeeMapper.findById(request.getId());
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        // 更新员工信息
        employee.setName(request.getName());
        employee.setGender(request.getGender());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setEducationId(request.getEducationId());
        employee.setPoliticalId(request.getPoliticalId());
        employee.setDeptId(request.getDeptId());
        employee.setPostId(request.getPostId());
        employee.setEntryDate(request.getEntryDate());
        employee.setStatus(request.getStatus());
        employee.setRemark(request.getRemark());

        // 设置更新者信息
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        employee.setUpdateBy(u.getUsername());

        employeeMapper.update(employee);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        // 检查员工是否存在
        Employee employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        employeeMapper.updateStatus(id, status);

        // 同步更新用户状态
        userMapper.updateStatus(employee.getUserId(), status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查员工是否存在
        Employee employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        // 删除员工信息
        employeeMapper.deleteById(id);

        // 删除关联的用户账号
        userMapper.deleteById(employee.getUserId());
    }

    @Override
    @DataScope(deptAlias = "e", userAlias = "e")
    public Workbook exportExcel(EmployeeQueryRequest request) {
        // 查询数据
        List<EmployeeVO> list = employeeMapper.findByCondition(request);

        // 定义表头
        String[] headers = {
                "工号", "姓名", "性别", "手机号", "邮箱",
                "学历", "政治面貌", "所属部门", "岗位",
                "入职日期", "状态", "备注"
        };

        // 创建Excel工作簿
        return ExcelUtils.createWorkbook("员工信息", headers, list, (row, data) -> {
            int colNum = 0;
            row.createCell(colNum++).setCellValue(data.getEmployeeNo());
            row.createCell(colNum++).setCellValue(data.getName());
            row.createCell(colNum++).setCellValue(data.getGender() == 1 ? "男" : "女");
            row.createCell(colNum++).setCellValue(data.getPhone());
            row.createCell(colNum++).setCellValue(data.getEmail());
            row.createCell(colNum++).setCellValue(data.getEducation());
            row.createCell(colNum++).setCellValue(data.getPolitical());
//            row.createCell(colNum++).setCellValue(data.getDeptName());
//            row.createCell(colNum++).setCellValue(data.getPostName());
            row.createCell(colNum++).setCellValue(data.getEntryDate().toString());
            row.createCell(colNum++).setCellValue(data.getStatus() == 1 ? "在职" : "离职");
            row.createCell(colNum).setCellValue(data.getRemark());
        });
    }

    @Override
    public List<Employee> findAllByDept() {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        return employeeMapper.findByDeptId(u.getDeptId());
    }

    private void validateEmployee(EmployeeAddRequest request) {
        if (request.getEmployeeNo() == null || request.getEmployeeNo().trim().isEmpty()) {
            throw new RuntimeException("工号不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (request.getPhone() != null && !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (request.getEmail() != null && !request.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new RuntimeException("邮箱格式不正确");
        }
        if (request.getDeptId() == null) {
            throw new RuntimeException("部门不能为空");
        }
        if (request.getPostId() == null) {
            throw new RuntimeException("岗位不能为空");
        }
        if (request.getEntryDate() == null) {
            throw new RuntimeException("入职日期不能为空");
        }
    }
} 