package com.wwm.eems.mapper;

import com.wwm.eems.model.Leave;
import com.wwm.eems.model.request.LeaveQueryRequest;
import com.wwm.eems.model.vo.LeaveVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface LeaveMapper {
    
    void insert(Leave leave);
    
    void update(Leave leave);
    
    Leave findById(@Param("id") Long id);
    
    List<LeaveVO> findByCondition(LeaveQueryRequest request);
    
    long countByCondition(LeaveQueryRequest request);
    
    List<Leave> findOverlap(@Param("employeeId") Long employeeId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);

    void delete(Long id);

    Integer getCurrentMonthLeaveDays(@Param("employeeId") Long employeeId);

    @MapKey("status_name")
    List<Map<String, Object>> getLeaveTypeCount(Long employeeId);

    Long countCurrentLeave(@Param("deptId") Long deptId);

    List<Map<String, Object>> getDailyLeaveStats(@Param("deptId") Long deptId);
}