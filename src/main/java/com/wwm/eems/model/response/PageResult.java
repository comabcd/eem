package com.wwm.eems.model.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private long total;      // 总记录数
    private List<T> list;    // 当前页数据
    private int pageNum;     // 当前页码
    private int pageSize;    // 每页大小
    private int pages;       // 总页数

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> build(List<T> list, long total, int pageNum, int pageSize) {
        return new PageResult<>(list, total, pageNum, pageSize);
    }
} 