package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.mapper.AttendanceMapper;
import com.wwm.eems.model.Department;
import com.wwm.eems.model.request.DeptAddRequest;
import com.wwm.eems.model.request.DeptUpdateRequest;
import com.wwm.eems.model.vo.AttendanceStatsVO;
import com.wwm.eems.model.vo.DeptStatisticsVO;
import com.wwm.eems.model.vo.DeptTreeVO;
import com.wwm.eems.service.DepartmentService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-03
 * @Description:部门相关
 * @Version: 1.0
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //获取部门树形结构
    @GetMapping("/tree")
    public Result<List<DeptTreeVO>> tree() {
        List<DeptTreeVO> tree = departmentService.getTree();
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        Department department = departmentService.findById(id);
        return Result.success(department);
    }

    //新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody DeptAddRequest request) {
        departmentService.add(request);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result<?> update(@RequestBody DeptUpdateRequest request) {
        departmentService.update(request);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        departmentService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<DeptStatisticsVO> getStatistics() {
        DeptStatisticsVO statistics = departmentService.getDeptStatistics();
        return Result.success(statistics);
    }

} 