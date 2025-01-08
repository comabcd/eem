package com.wwm.eems.mapper;

import com.wwm.eems.model.DictData;
import com.wwm.eems.model.request.DictDataRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: 229970631王伟铭
 * @CreateTime: 2025-01-02
 * @Description:
 * @Version: 1.0
 */
@Mapper
public interface DictMapper {
    List<String> getDictType();

    List<DictData> findAll(DictDataRequest request);

    List<DictData> findByType(@Param("dictType") String dictType);

    DictData findById(@Param("id") Long id);

    void insert(DictData dictData);

    void update(DictData dictData);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void deleteById(@Param("id") Long id);
}
