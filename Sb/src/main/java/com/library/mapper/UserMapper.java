package com.library.mapper;

import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据用户名和密码查询用户
     */
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);
    
    /**
     * 更新用户信息
     */
    int update(User user);
    
    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询用户列表
     */
    List<User> findByPage(@Param("keyword") String keyword, @Param("userType") Integer userType);
    
    /**
     * 统计用户数量
     */
    int countUsers(@Param("userType") Integer userType);
    
    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}