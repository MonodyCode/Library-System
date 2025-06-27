package com.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(String username, String password) {
        // MD5加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        return userMapper.findByUsernameAndPassword(username, encryptedPassword);
    }
    
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (checkUsernameExists(user.getUsername())) {
            return false;
        }
        
        // MD5加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(encryptedPassword);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        return userMapper.insert(user) > 0;
    }
    
    @Override
    @Cacheable(value = "users", key = "#id")
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    @Override
    @CacheEvict(value = "users", key = "#user.id")
    public boolean updateUser(User user) {
        user.setUpdateTime(new Date());
        return userMapper.update(user) > 0;
    }
    
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    
    @Override
    public PageInfo<User> findUsersByPage(PageQuery pageQuery, Integer userType) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<User> users = userMapper.findByPage(pageQuery.getKeyword(), userType);
        return new PageInfo<>(users);
    }
    
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public boolean updateUserStatus(Long id, Integer status) {
        return userMapper.updateStatus(id, status) > 0;
    }
    
    @Override
    public boolean checkUsernameExists(String username) {
        return userMapper.findByUsername(username) != null;
    }
    
    @Override
    public int countUsers(Integer userType) {
        return userMapper.countUsers(userType);
    }
}