package com.wwm.eems.service;

import com.wwm.eems.model.Attendance;
import com.wwm.eems.model.request.AttendanceAddRequest;
import com.wwm.eems.model.request.AttendanceEmployeeQueryRequest;
import com.wwm.eems.model.request.AttendanceQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.AttendanceEmployeeVO;
import com.wwm.eems.model.vo.AttendanceStatsVO;
import com.wwm.eems.model.vo.AttendanceVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    
    void checkIn(AttendanceAddRequest request);
    
    void checkOut(AttendanceAddRequest request);
    
    Attendance findById(Long id);
    
    Attendance findTodayRecord();
    
    PageResult<AttendanceVO> findByCondition(AttendanceQueryRequest request);
    
    List<AttendanceEmployeeVO> findMyAttendance(AttendanceEmployeeQueryRequest request);

    AttendanceStatsVO getMonthlyAttendanceStats();

    Map<String, Object> getMonthlyAttendance(Long id, LocalDate firstDay, LocalDate lastDay);
}