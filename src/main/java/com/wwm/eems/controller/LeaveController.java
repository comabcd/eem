package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.model.Leave;
import com.wwm.eems.model.request.LeaveAddRequest;
import com.wwm.eems.model.request.LeaveApproveRequest;
import com.wwm.eems.model.request.LeaveQueryRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.LeaveStatsVO;
import com.wwm.eems.model.vo.LeaveVO;
import com.wwm.eems.service.LeaveService;
import lombok.Data;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    //申请
    @PostMapping("/apply")
    public Result<?> apply(@RequestBody LeaveAddRequest request) {
        leaveService.apply(request);
        return Result.success();
    }

    //同意
    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id) {
        leaveService.approve(id);
        return Result.success();
    }

    //拒绝
    @PutMapping("/reject/{id}")
    public Result<?> reject(@PathVariable Long id, @RequestBody LeaveApproveRequest request) {
        leaveService.reject(id, request.getApproveRemark());
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        leaveService.cancel(id);
        return Result.success();
    }

    //销假
    @PutMapping("/complete/{id}")
    public Result<?> complete(@PathVariable Long id) {
        leaveService.complete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Leave> getById(@PathVariable Long id) {
        Leave leave = leaveService.findById(id);
        return Result.success(leave);
    }

    @GetMapping("/search")
    public Result<PageResult<LeaveVO>> search(LeaveQueryRequest request) {
        PageResult<LeaveVO> pageResult = leaveService.findByCondition(request);
        pageResult.getList().forEach(System.out::println);
        return Result.success(pageResult);
    }

    /**
     * 获取自己的请假记录
     *
     * @param request
     * @return
     */
    @GetMapping("/my")
    public Result<List<LeaveVO>> getMyLeaves(LeaveQueryRequest request) {
        List<LeaveVO> list = leaveService.findMyLeaves(request);
        return Result.success(list);
    }

    @Data
    public static class LeaveStatistics {
        Integer days;
        List<Map<String, Object>> list;
    }

    @GetMapping("/leavesStatistics")
    public Result<?> getLeaveStatistics() {
        Integer days = leaveService.getCurrentMonthLeaveDays();
        List<Map<String, Object>> list = leaveService.getLeaveTypeCount();

        LeaveStatistics data = new LeaveStatistics();
        data.setDays(days);
        data.setList(list);

        return Result.success(data);
    }

    @GetMapping("/stats")
    public Result<LeaveStatsVO> getLeaveStats() {
        LeaveStatsVO stats = leaveService.getMonthlyLeaveStats();
        return Result.success(stats);
    }

} 