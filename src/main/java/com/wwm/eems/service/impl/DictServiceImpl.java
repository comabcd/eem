package com.wwm.eems.service.impl;

import com.wwm.eems.mapper.DictMapper;
import com.wwm.eems.model.DictData;
import com.wwm.eems.model.request.DictDataAddRequest;
import com.wwm.eems.model.request.DictDataRequest;
import com.wwm.eems.model.request.DictDataUpdateRequest;
import com.wwm.eems.model.vo.DictTreeVO;
import com.wwm.eems.service.DictService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:
 * @Version: 1.0
 */
@Service
public class DictServiceImpl implements DictService {
    @Autowired
    private DictMapper dictMapper;

    @Override
    public List<String> getDictType() {
        return dictMapper.getDictType();
    }

    @Override
    public List<DictData> findAll(DictDataRequest request) {
        return dictMapper.findAll(request);
    }

    @Override
    public List<DictData> findByType(String dictType) {
        return dictMapper.findByType(dictType);
    }

    @Override
    public DictData findById(Long id) {
        return dictMapper.findById(id);
    }

    @Override
    @Transactional
    public void add(DictDataAddRequest request) {

        DictData dictData = new DictData();
        dictData.setDictType(request.getDictType());
        dictData.setDictName(request.getDictName());
        dictData.setOrderNum(request.getOrderNum());
        dictData.setStatus(1); // 默认启用

        String username = SecurityUtils.getSubject().getPrincipal().toString();
        dictData.setCreateBy(username);
        dictData.setCreateTime(LocalDateTime.now());


        dictMapper.insert(dictData);
    }

    @Override
    @Transactional
    public void update(DictDataUpdateRequest request) {
        // 检查数据数据是否存在
        DictData dictData = dictMapper.findById(request.getId());
        if (dictData == null) {
            throw new RuntimeException("数据数据不存在");
        }

        dictData.setDictType(request.getDictType());
        dictData.setOrderNum(request.getOrderNum());
        dictData.setStatus(request.getStatus());


        String username = SecurityUtils.getSubject().getPrincipal().toString();


        dictMapper.update(dictData);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        // 检查数据数据是否存在
        DictData dictData = dictMapper.findById(id);
        if (dictData == null) {
            throw new RuntimeException("数据数据不存在");
        }

        dictMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查数据数据是否存在
        DictData dictData = dictMapper.findById(id);
        if (dictData == null) {
            throw new RuntimeException("数据数据不存在");
        }

        dictMapper.deleteById(id);
    }

}
