package com.wwm.eems.mapper;

import com.wwm.eems.model.Employee;
import com.wwm.eems.model.request.EmployeeQueryRequest;
import com.wwm.eems.model.vo.EmployeeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeMapper {
    
    List<Employee> findAll(Employee employee);
    
    Employee findById(@Param("id") Long id);
    
    Employee findByNo(@Param("employeeNo") String employeeNo);
    
    Employee findByUserId(@Param("userId") Long userId);
    
    List<Employee> findByDeptId(@Param("deptId") Long deptId);
    
    List<Employee> findByPostId(@Param("postId") Long postId);
    
    void insert(Employee employee);
    
    void update(Employee employee);
    
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    void deleteById(@Param("id") Long id);
    
    List<EmployeeVO> findByCondition(EmployeeQueryRequest request);
    
    long countByCondition(EmployeeQueryRequest request);

    Employee findByUsername(@Param("username") String username);

    Long getMonthlyOnboard();

    Long getMonthlyResign();

    Long getMonthBeginningCount();

    List<Map<String, Object>> getOnboardTrend();
}