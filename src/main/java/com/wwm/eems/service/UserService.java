package com.wwm.eems.service;


import com.wwm.eems.model.User;
import com.wwm.eems.model.request.LoginRequest;
import com.wwm.eems.model.request.PasswordUpdateRequest;
import com.wwm.eems.model.request.UserAddRequest;
import com.wwm.eems.model.request.UserUpdateRequest;
import com.wwm.eems.model.request.RegisterRequest;
import com.wwm.eems.model.response.LoginResponse;

import java.util.List;

public interface UserService {
    
    User findByUsername(String username);
    
    List<User> findAll();
    
    LoginResponse login(LoginRequest request);
    
    void add(UserAddRequest request);
    
    void update(UserUpdateRequest request);
    
    void updatePassword(PasswordUpdateRequest request);
    
    void updateStatus(Long id, Integer status);
    
    void delete(Long id);
    
    User getByUsername(String username);

    void resetPassword(Long id);
}