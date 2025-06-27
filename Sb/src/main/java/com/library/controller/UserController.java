package com.library.controller;

import com.github.pagehelper.PageInfo;
import com.library.common.PageQuery;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        User user = userService.login(username, password);
        if (user != null && user.getStatus() == 1) {
            // 登录成功，保存用户信息到session
            session.setAttribute("user", user);
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", session.getId());
            
            return Result.success("登录成功", data);
        } else {
            return Result.error("用户名或密码错误，或账户已被禁用");
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        if (userService.checkUsernameExists(user.getUsername())) {
            return Result.error("用户名已存在");
        }
        
        // 默认注册为读者
        if (user.getUserType() == null) {
            user.setUserType(1);
        }
        
        if (userService.register(user)) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("登出成功");
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.unauthorized();
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        // 只能更新自己的信息，或管理员可以更新任何用户信息
        if (!currentUser.getId().equals(user.getId()) && currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (userService.updateUser(user)) {
            // 如果更新的是当前用户，更新session中的用户信息
            if (currentUser.getId().equals(user.getId())) {
                User updatedUser = userService.findById(user.getId());
                session.setAttribute("user", updatedUser);
            }
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 分页查询用户列表（管理员功能）
     */
    @GetMapping("/list")
    public Result<PageInfo<User>> getUserList(PageQuery pageQuery, 
                                             @RequestParam(required = false) Integer userType,
                                             HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        PageInfo<User> pageInfo = userService.findUsersByPage(pageQuery, userType);
        return Result.success(pageInfo);
    }
    
    /**
     * 删除用户（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (userService.deleteUser(id)) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 更新用户状态（管理员功能）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, 
                                          @RequestParam Integer status,
                                          HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        if (currentUser.getUserType() != 2) {
            return Result.forbidden();
        }
        
        if (userService.updateUserStatus(id, status)) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
}