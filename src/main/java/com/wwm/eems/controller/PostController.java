package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.model.Post;
import com.wwm.eems.model.request.PostAddRequest;
import com.wwm.eems.model.request.PostQueryRequest;
import com.wwm.eems.model.request.PostUpdateRequest;
import com.wwm.eems.model.response.PageResult;
import com.wwm.eems.model.vo.PostVO;
import com.wwm.eems.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping ("/list")
    public Result<PageResult<PostVO>> list(PostQueryRequest request) {
        PageResult<PostVO> posts = postService.findAll(request);
        return Result.success(posts);
    }

    @GetMapping("/search")
    public Result<List<Post>> search(Long deptId){
        return Result.success(postService.getByDeptId(deptId));
    }

    @GetMapping("/{id}")
    public Result<Post> getById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return Result.success(post);
    }

    @PostMapping("/add")
    //@RequiresRoles("1")  // 系统管理员
    public Result<?> add(@RequestBody PostAddRequest request) {
        postService.add(request);
        return Result.success();
    }

    @PutMapping("/update")
    //@RequiresRoles("1")
    public Result<?> update(@RequestBody PostUpdateRequest request) {
        postService.update(request);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")
    //@RequiresRoles("1")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        postService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        postService.delete(id);
        return Result.success();
    }
} 