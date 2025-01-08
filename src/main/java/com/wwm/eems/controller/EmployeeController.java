package com.wwm.eems.controller;


import com.wwm.eems.common.Result;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.request.EmployeeAddRequest;
import com.wwm.eems.model.request.EmployeeQueryRequest;
import com.wwm.eems.model.request.EmployeeUpdateRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.EmployeeVO;
import com.wwm.eems.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:员工相关
 * @Version: 1.0
 */

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/listWithDept")
    public Result<List<Employee>> listWithDept(){
        List<Employee> employees = employeeService.findAllByDept();
        return Result.success(employees);
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        return Result.success(employee);
    }

    @GetMapping("/dept/{deptId}")
    public Result<List<Employee>> getByDeptId(@PathVariable Long deptId) {
        List<Employee> employees = employeeService.findByDeptId(deptId);
        return Result.success(employees);
    }


    @PutMapping("/update")
    public Result<?> update(@RequestBody EmployeeUpdateRequest request) {
        employeeService.update(request);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        employeeService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/search")
    public Result<PageResult<EmployeeVO>> search(EmployeeQueryRequest request) {

        PageResult<EmployeeVO> employees = employeeService.findByCondition(request);

        return Result.success(employees);
    }

    @PostMapping("/export")
    //@RequiresRoles({"1", "2", "3"})  // 系统管理员、部门管理员、人力资源管理员
    public void export(@RequestBody EmployeeQueryRequest request, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");
        
        // 导出Excel
        Workbook workbook = employeeService.exportExcel(request);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
} 