package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.pojo.User;
import com.bookstore.service.EmailService;
import com.bookstore.service.UserService;
import com.bookstore.util.TokenUtil;
import com.bookstore.util.VerificationStore;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationStore verificationStore;

    public UserController(UserService userService, EmailService emailService,
                          VerificationStore verificationStore) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationStore = verificationStore;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        // 生成令牌，放入返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        data.put("role", user.getRole());
        // 使用 service 层持久化的 token
        data.put("token", user.getToken());
        return Result.success(data);
    }

    /**
     * 用户列表（管理端）
     */
    @GetMapping("/list")
    public Result<java.util.List<User>> list() {
        java.util.List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send-verify-code")
    public Result<Void> sendVerifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            delayResponse();
            return Result.error(400, "邮箱不能为空");
        }
        // 60秒冷却检查
        if (verificationStore.isInCooldown(email)) {
            delayResponse();
            return Result.error(429, "发送过于频繁，请 60 秒后再试");
        }
        // 生成 6 位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        verificationStore.put(email, code);
        emailService.sendVerifyCode(email, code);
        return Result.success(null);
    }

    /**
     * 用户注册（需邮箱验证码）
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        try {
            User user = new User();
            user.setUsername(body.get("username"));
            user.setPassword(body.get("password"));
            user.setPhone(body.get("phone"));
            user.setEmail(body.get("email"));
            User registered = userService.register(user, code);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", registered.getUserId());
            data.put("username", registered.getUsername());
            data.put("phone", registered.getPhone());
            data.put("token", registered.getToken());
            return Result.success(data);
        } catch (RuntimeException e) {
            delayResponse();
            return Result.error(400, e.getMessage());
        }
    }

    // 延迟响应（防暴力破解）
    private void delayResponse() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }
}
