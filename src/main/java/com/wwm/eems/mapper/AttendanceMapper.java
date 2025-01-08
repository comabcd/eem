package com.wwm.eems.mapper;

import com.wwm.eems.model.Attendance;
import com.wwm.eems.model.request.AttendanceQueryRequest;
import com.wwm.eems.model.vo.AttendanceEmployeeVO;
import com.wwm.eems.model.vo.AttendanceStatsVO;
import com.wwm.eems.model.vo.AttendanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    void insert(Attendance attendance);

    void update(Attendance attendance);

    Attendance findById(@Param("id") Long id);

    Attendance findTodayRecord(@Param("employeeId") Long employeeId);

    List<AttendanceVO> findByCondition(AttendanceQueryRequest request);

    long countByCondition(AttendanceQueryRequest request);

    List<AttendanceEmployeeVO> findOwnByCondition(@Param("employeeId") Long employeeId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("status") Integer status);

    Long countTodayAbnormal(@Param("deptId") Long deptId);

    AttendanceStatsVO getMonthlyStats(@Param("deptId") Long deptId,@Param("empId") Long empId);

    List<Attendance> selectMonthlyAttendance(@Param("empId") Long empId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}