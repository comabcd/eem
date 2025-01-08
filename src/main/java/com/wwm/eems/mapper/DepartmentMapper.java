package com.wwm.eems.mapper;

import com.wwm.eems.model.Department;
import com.wwm.eems.model.vo.DeptDistributionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    
    List<Department> findAll();
    
    Department findById(@Param("id") Long id);
    
    List<Department> findByParentId(@Param("parentId") Long parentId);
    
    void insert(Department department);
    
    void update(Department department);
    
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    void deleteById(@Param("id") Long id);
    
    List<Department> findChildren(@Param("parentId") Long parentId);

    Department findByName(@Param("department") String department);

    Long getTotalLeafDepartments();

    List<DeptDistributionVO> getDeptDistribution();
}