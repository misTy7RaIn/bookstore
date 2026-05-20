package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.pojo.User;
import com.bookstore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器：管理员登录、管理员账号管理
 */
@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /** 获取当前登录用户 */
    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute("currentUser");
    }

    /**
     * 管理员登录（role >= 2 可登录）
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        User user = userService.loginAsAdmin(loginUser.getUsername(), loginUser.getPassword());
        if (user == null) {
            return Result.error(401, "用户名或密码错误，或非管理员账号");
        }
        return Result.success(user);
    }

    /**
     * 创建管理员账号（仅超级管理员）
     */
    @PostMapping("/create")
    public Result<User> create(@RequestBody Map<String, String> body, HttpServletRequest request) {
        if (currentUser(request).getRole() < 3) {
            return Result.error(403, "仅超级管理员可创建管理员账号");
        }
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(body.get("password"));
        user.setPhone(body.getOrDefault("phone", ""));
        user.setEmail(body.getOrDefault("email", ""));
        User created = userService.createAdmin(user);
        return Result.success(created);
    }

    /**
     * 删除管理员（仅超级管理员，不可删除自己）
     */
    @DeleteMapping("/delete/{userId}")
    public Result<?> delete(@PathVariable Integer userId, HttpServletRequest request) {
        if (currentUser(request).getRole() < 3) {
            return Result.error(403, "仅超级管理员可删除管理员");
        }
        userService.deleteAdmin(userId, currentUser(request).getUserId());
        return Result.success();
    }

    /**
     * 修改管理员信息（仅超级管理员）
     */
    @PutMapping("/update/{userId}")
    public Result<?> update(@PathVariable Integer userId, @RequestBody Map<String, String> body,
                            HttpServletRequest request) {
        if (currentUser(request).getRole() < 3) {
            return Result.error(403, "仅超级管理员可修改管理员信息");
        }
        User user = new User();
        user.setUserId(userId);
        user.setUsername(body.get("username"));
        user.setPassword(body.get("password"));
        user.setPhone(body.getOrDefault("phone", ""));
        user.setEmail(body.getOrDefault("email", ""));
        userService.updateAdmin(user);
        return Result.success();
    }

    /**
     * 列出所有管理员（role >= 2）。超级管理员可见密码
     */
    @GetMapping("/list")
    public Result<List<User>> list(HttpServletRequest request) {
        User cur = currentUser(request);
        List<User> admins = userService.listAdmins(cur.getRole());
        return Result.success(admins);
    }
}
