package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.mapper.DepartmentMapper;
import com.wwm.eems.model.request.OnboardRequest;
import com.wwm.eems.model.vo.DeptDistributionVO;
import com.wwm.eems.model.vo.DeptTreeVO;
import com.wwm.eems.model.vo.HrStatisticsVO;
import com.wwm.eems.model.vo.OnboardTrendVO;
import com.wwm.eems.service.DepartmentService;
import com.wwm.eems.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-04
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/hr")
public class HrController {
    @Autowired
    private HrService hrService;

    /**
     * 入职办理
     *
     * @param request
     * @return
     */
    @PostMapping("/onboard")
    public Result onboard(@RequestBody OnboardRequest request) {
        System.out.println("============HrController:onboard:OnboardRequest============");
        System.out.println(request);
        System.out.println("============HrController:onboard:OnboardRequest============");

        hrService.onboard(request);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<HrStatisticsVO> getStatistics() {
        HrStatisticsVO statistics = hrService.getHrStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/dept/distribution")
    public Result<List<DeptDistributionVO>> getDeptDistribution() {
        List<DeptDistributionVO> distribution = hrService.getDeptDistribution();
        return Result.success(distribution);
    }

    @GetMapping("/onboard/trend")
    public Result<OnboardTrendVO> getOnboardTrend() {
        OnboardTrendVO trend = hrService.getOnboardTrend();
        return Result.success(trend);
    }

    @DeleteMapping("/resign/{id}")
    public Result<?> resign(@PathVariable Long id){
        hrService.resign(id);
        return Result.success();
    }

}
