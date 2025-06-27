package com.library.service;

import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.entity.User;

public interface UserService {
    
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 根据ID查询用户
     */
    User findById(Long id);
    
    /**
     * 更新用户信息
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
    
    /**
     * 分页查询用户列表
     */
    PageInfo<User> findUsersByPage(PageQuery pageQuery, Integer userType);
    
    /**
     * 更新用户状态
     */
    boolean updateUserStatus(Long id, Integer status);
    
    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);
    
    /**
     * 统计用户数量
     */
    int countUsers(Integer userType);
}