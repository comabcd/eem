package com.wwm.eems.service;

import com.wwm.eems.model.Post;
import com.wwm.eems.model.request.PostAddRequest;
import com.wwm.eems.model.request.PostQueryRequest;
import com.wwm.eems.model.request.PostUpdateRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.PostVO;

import java.util.List;

public interface PostService {
    
    PageResult<PostVO> findAll(PostQueryRequest request);
    
    Post findById(Long id);
    
    void add(PostAddRequest request);
    
    void update(PostUpdateRequest request);
    
    void updateStatus(Long id, Integer status);
    
    void delete(Long id);

    List<Post> getByDeptId(Long deptId);
}