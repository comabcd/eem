package com.wwm.eems.service;

import com.wwm.eems.model.DictData;
import com.wwm.eems.model.request.DictDataAddRequest;
import com.wwm.eems.model.request.DictDataRequest;
import com.wwm.eems.model.request.DictDataUpdateRequest;
import com.wwm.eems.model.vo.DictTreeVO;

import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:
 * @Version: 1.0
 */
public interface DictService {
    List<String> getDictType();

    List<DictData> findAll(DictDataRequest request);

    List<DictData> findByType(String dictType);

    DictData findById(Long id);

    void add(DictDataAddRequest request);

    void update(DictDataUpdateRequest request);

    void updateStatus(Long id, Integer status);

    void delete(Long id);

}
