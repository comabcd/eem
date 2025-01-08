package com.wwm.eems.service.impl;

import com.wwm.eems.mapper.EmployeeMapper;
import com.wwm.eems.mapper.PostMapper;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.Post;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.PostAddRequest;
import com.wwm.eems.model.request.PostQueryRequest;
import com.wwm.eems.model.request.PostUpdateRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.PostVO;
import com.wwm.eems.service.PostService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public PageResult<PostVO> findAll(PostQueryRequest request) {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        if (u.getRole() == 2) {
            Long deptId = employeeMapper.findByUsername(u.getUsername()).getDeptId();
            request.setDeptId(deptId);
        }

        // 查询总记录数
        long total = postMapper.countAll(request);

        List<PostVO> list = postMapper.findAll(request);

        // 构建分页结果
        return PageResult.build(list, total, request.getPageNum(), request.getPageSize());

    }

    @Override
    public Post findById(Long id) {
        return postMapper.findById(id);
    }

    @Override
    @Transactional
    public void add(PostAddRequest request) {
        // 检查岗位编码是否已存在
        Post existByCode = postMapper.findByCode(request.getPostCode());
        if (existByCode != null) {
            throw new RuntimeException("岗位编码已存在");
        }

        // 检查岗位名称是否已存在
        Post existByName = postMapper.findByName(request.getPostName());
        if (existByName != null) {
            throw new RuntimeException("岗位名称已存在");
        }

        // 创建岗位对象
        Post post = new Post();
        post.setPostCode(request.getPostCode());
        post.setPostName(request.getPostName());
        post.setDeptId(request.getDeptId());
        post.setOrderNum(request.getOrderNum());
        post.setStatus(1); // 默认启用

        // 设置创建者信息
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        post.setCreateBy(u.getUsername());
        post.setCreateTime(LocalDateTime.now());
        post.setRemark(request.getRemark());

        postMapper.insert(post);
    }

    @Override
    @Transactional
    public void update(PostUpdateRequest request) {
        // 检查岗位是否存在
        Post post = postMapper.findById(request.getId());
        if (post == null) {
            throw new RuntimeException("岗位不存在");
        }

        // 检查岗位编码是否重复
        if (!post.getPostCode().equals(request.getPostCode())) {
            Post existByCode = postMapper.findByCode(request.getPostCode());
            if (existByCode != null) {
                throw new RuntimeException("岗位编码已存在");
            }
        }

        // 检查岗位名称是否重复
        if (!post.getPostName().equals(request.getPostName())) {
            Post existByName = postMapper.findByName(request.getPostName());
            if (existByName != null) {
                throw new RuntimeException("岗位名称已存在");
            }
        }

        // 更新岗位信息
        post.setPostCode(request.getPostCode());
        post.setPostName(request.getPostName());
        post.setOrderNum(request.getOrderNum());
        post.setStatus(request.getStatus());
        post.setRemark(request.getRemark());

        // 设置更新者信息
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        post.setCreateBy(u.getUsername());
        post.setUpdateBy(u.getUpdateBy());

        postMapper.update(post);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        // 检查岗位是否存在
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new RuntimeException("岗位不存在");
        }

        postMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查岗位是否存在
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new RuntimeException("岗位不存在");
        }

        // TODO: 检查是否有员工关联此岗位，如果有则不能删除
        List<Employee> list = employeeMapper.findByPostId(id);
        if (list != null) {
            throw new RuntimeException("岗位下存在员工，不能删除");
        }

        postMapper.deleteById(id);
    }

    @Override
    public List<Post> getByDeptId(Long deptId) {
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        if(u.getRole()==2){
            Employee e = employeeMapper.findByUsername(u.getUsername());
            deptId = e.getDeptId();
        }

        return postMapper.getByDeptId(deptId);
    }
} 