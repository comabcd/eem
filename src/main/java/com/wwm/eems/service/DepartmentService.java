package com.wwm.eems.service;

import com.wwm.eems.model.Department;
import com.wwm.eems.model.request.DeptAddRequest;
import com.wwm.eems.model.request.DeptUpdateRequest;
import com.wwm.eems.model.vo.DeptStatisticsVO;
import com.wwm.eems.model.vo.DeptTreeVO;
import java.util.List;

public interface DepartmentService {
    
    List<Department> findAll();
    
    Department findById(Long id);
    
    List<DeptTreeVO> getTree();
    
    void add(DeptAddRequest request);
    
    void update(DeptUpdateRequest request);
    
    void updateStatus(Long id, Integer status);
    
    void delete(Long id);

    DeptStatisticsVO getDeptStatistics();
}