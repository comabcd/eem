package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.model.DictData;
import com.wwm.eems.model.request.DictDataAddRequest;
import com.wwm.eems.model.request.DictDataRequest;
import com.wwm.eems.model.request.DictDataUpdateRequest;
import com.wwm.eems.model.vo.DeptTreeVO;
import com.wwm.eems.model.vo.DictTreeVO;
import com.wwm.eems.service.DictService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/dict")
public class DictController {
    @Autowired
    private DictService dictService;


    @GetMapping("/getDictType")
    public Result<List<String>> getDictType() {
        return Result.success(dictService.getDictType());
    }

    @GetMapping("/list")
    public Result<List<DictData>> findAll(DictDataRequest request) {
        return Result.success(dictService.findAll(request));
    }

    @GetMapping("/type/{dictType}")
    public Result<List<DictData>> getByType(@PathVariable String dictType) {
        List<DictData> dataList = dictService.findByType(dictType);
        return Result.success(dataList);
    }

    @GetMapping("/{id}")
    public Result<DictData> getById(@PathVariable Long id) {
        DictData data = dictService.findById(id);
        return Result.success(data);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody DictDataAddRequest request) {
        dictService.add(request);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody DictDataUpdateRequest request) {
        dictService.update(request);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        dictService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dictService.delete(id);
        return Result.success();
    }
}
