package com.wwm.eems.service;

import com.wwm.eems.model.Leave;
import com.wwm.eems.model.request.LeaveAddRequest;
import com.wwm.eems.model.request.LeaveQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.LeaveStatsVO;
import com.wwm.eems.model.vo.LeaveVO;
import java.util.List;
import java.util.Map;

public interface LeaveService {
    
    void apply(LeaveAddRequest request);
    
    void approve(Long id);
    
    Leave findById(Long id);
    
    PageResult<LeaveVO> findByCondition(LeaveQueryRequest request);
    
    List<LeaveVO> findMyLeaves(LeaveQueryRequest request);

    void reject(Long id, String approveRemark);

    void cancel(Long id);

    void complete(Long id);

    Integer getCurrentMonthLeaveDays();

    List<Map<String, Object>> getLeaveTypeCount();

    LeaveStatsVO getMonthlyLeaveStats();
}