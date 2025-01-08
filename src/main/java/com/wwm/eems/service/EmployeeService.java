package com.wwm.eems.service;

import com.wwm.eems.model.Employee;
import com.wwm.eems.model.request.EmployeeAddRequest;
import com.wwm.eems.model.request.EmployeeUpdateRequest;
import com.wwm.eems.model.request.EmployeeQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.EmployeeVO;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;

public interface EmployeeService {
    
//    List<Employee> findAll(EmployeeQueryRequest request);
    
    Employee findById(Long id);
    
    List<Employee> findByDeptId(Long deptId);
    
    void update(EmployeeUpdateRequest request);
    
    void updateStatus(Long id, Integer status);
    
    void delete(Long id);
    
    PageResult<EmployeeVO> findByCondition(EmployeeQueryRequest request);
    
    Workbook exportExcel(EmployeeQueryRequest request);

    List<Employee> findAllByDept();
}