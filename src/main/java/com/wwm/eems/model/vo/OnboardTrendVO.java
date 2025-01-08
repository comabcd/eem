package com.wwm.eems.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-06
 * @Description: 入职趋势数据
 * @Version: 1.0
 */
@Data
public class OnboardTrendVO {
    private List<String> dates;     // 日期列表
    private List<Long> counts;      // 每日入职人数

    public OnboardTrendVO() {
        this.dates = new ArrayList<>();
        this.counts = new ArrayList<>();
    }
}
