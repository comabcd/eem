package com.wwm.eems.service;

import com.wwm.eems.model.request.OnboardRequest;
import com.wwm.eems.model.vo.DeptDistributionVO;
import com.wwm.eems.model.vo.HrStatisticsVO;
import com.wwm.eems.model.vo.OnboardTrendVO;

import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-04
 * @Description:
 * @Version: 1.0
 */
public interface HrService {
    void onboard(OnboardRequest request);

    HrStatisticsVO getHrStatistics();

    List<DeptDistributionVO> getDeptDistribution();

    OnboardTrendVO getOnboardTrend();

    void resign(Long id);
}
