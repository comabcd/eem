package com.wwm.eems.mapper;

import com.wwm.eems.model.Post;
import com.wwm.eems.model.request.PostQueryRequest;
import com.wwm.eems.model.vo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostMapper {
    
    List<PostVO> findAll(PostQueryRequest request);
    
    Post findById(@Param("id") Long id);
    
    Post findByCode(@Param("postCode") String postCode);
    
    Post findByName(@Param("postName") String postName);
    
    void insert(Post post);
    
    void update(Post post);
    
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    void deleteById(@Param("id") Long id);

    Long countAll(PostQueryRequest request);

    List<Post> getByDeptId(@Param("deptId") Long deptId);
}