package com.wwm.eems.controller;

import com.wwm.eems.common.Result;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.PasswordUpdateRequest;
import com.wwm.eems.model.request.UserAddRequest;
import com.wwm.eems.model.request.UserUpdateRequest;
import com.wwm.eems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody UserAddRequest request) {
        userService.add(request);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody UserUpdateRequest request) {
        userService.update(request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return Result.success();
    }

    @PutMapping("/status/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @PutMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }
} 