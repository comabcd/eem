package com.wwm.eems.service.impl;


import com.wwm.eems.common.PasswordUtils;
import com.wwm.eems.mapper.EmployeeMapper;
import com.wwm.eems.mapper.UserMapper;
import com.wwm.eems.model.Employee;
import com.wwm.eems.model.User;
import com.wwm.eems.model.request.*;
import com.wwm.eems.model.response.LoginResponse;
import com.wwm.eems.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // 执行登录
            Subject subject = SecurityUtils.getSubject();

            System.out.println("==================================");
            System.out.println(subject);
            System.out.println("==================================");

            UsernamePasswordToken token = new UsernamePasswordToken(
                    request.getUsername(),
                    request.getPassword()
            );
            subject.login(token);

            // 获取登录用户信息
            User user = (User) subject.getPrincipal();

            // 构建返回结果
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setRole(user.getRole());
            response.setToken(subject.getSession().getId().toString());

            return response;
        } catch (UnknownAccountException e) {
            throw new RuntimeException("用户不存在");
        } catch (IncorrectCredentialsException e) {
            throw new RuntimeException("密码错误");
        } catch (DisabledAccountException e) {
            throw new RuntimeException("账号已被禁用");
        } catch (AuthenticationException e) {
            throw new RuntimeException("登录失败");
        }
    }

    @Override
    public void add(UserAddRequest request) {
        // 检查用户名是否已存在
        User existUser = findByUsername(request.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        // 加密密码
        user.setPassword(PasswordUtils.encrypt(request.getPassword(), request.getUsername()));
        user.setRealName(request.getRealName());
        user.setRole(request.getRole());
        user.setStatus(1); // 默认启用

        // 保存用户
        userMapper.insert(user);
    }

    @Override
    public void update(UserUpdateRequest request) {
        // 检查用户是否存在
        User user = userMapper.findById(request.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setRemark(request.getRemark());

        userMapper.update(user);
    }

    @Override
    public void updatePassword(PasswordUpdateRequest request) {
        // 获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        // 验证旧密码
        if (!PasswordUtils.matches(request.getOldPassword(), user.getUsername(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 加密新密码
        String encryptedPassword = PasswordUtils.encrypt(request.getNewPassword(), user.getUsername());
        userMapper.updatePassword(user.getId(), encryptedPassword);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        // 检查用户是否存在
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能禁用系统管理员
        if (user.getRole() == 1 && status == 0) {
            throw new RuntimeException("不能禁用系统管理员");
        }

        userMapper.updateStatus(id, status);
    }

    @Override
    public void delete(Long id) {
        // 检查用户是否存在
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能删除系统管理员
        if (user.getRole() == 1) {
            throw new RuntimeException("不能删除系统管理员");
        }

        userMapper.deleteById(id);
    }


    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        if (request.getEmployeeNo() == null || request.getEmployeeNo().trim().isEmpty()) {
            throw new RuntimeException("工号不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (request.getPhone() != null && !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (request.getEmail() != null && !request.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new RuntimeException("邮箱格式不正确");
        }
        if (request.getDeptId() == null) {
            throw new RuntimeException("部门不能为空");
        }
        if (request.getPostId() == null) {
            throw new RuntimeException("岗位不能为空");
        }
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void resetPassword(Long id) {

        User user = new User();
        user.setId(id);
        // 加密新密码
        String encryptedPassword = PasswordUtils.encrypt("123456", userMapper.findById(id).getUsername());
        user.setPassword(encryptedPassword);

        User u = (User) SecurityUtils.getSubject().getPrincipal();

        user.setUpdateBy(u.getUsername());
        u.setUpdateTime(LocalDateTime.now());

        userMapper.resetPassword(user);
    }


} 