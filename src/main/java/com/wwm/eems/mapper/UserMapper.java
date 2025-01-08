package com.wwm.eems.mapper;

import com.wwm.eems.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    
    List<User> findAll();
    
    void insert(User user);
    
    void update(User user);
    
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    void updatePassword(@Param("id") Long id, @Param("password") String password);
    
    void deleteById(@Param("id") Long id);
    
    User findById(@Param("id") Long id);
    
    User findByUsername(@Param("username") String username);

    void resetPassword(User user);
}