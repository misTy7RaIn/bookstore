package com.bookstore.service.impl;

import com.bookstore.mapper.UserMapper;
import com.bookstore.pojo.User;
import com.bookstore.service.UserService;
import com.bookstore.util.TokenUtil;
import com.bookstore.util.VerificationStore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationStore verificationStore;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder,
                           VerificationStore verificationStore) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.verificationStore = verificationStore;
    }

    /**
     * 商城用户登录：仅 role=1 可登录，管理员账号被拒绝
     */
    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        // 拒绝管理员账号在商城登录
        if (user.getRole() != 1) {
            return null;
        }
        String token = TokenUtil.generate();
        user.setToken(token);
        userMapper.updateToken(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 管理员登录：role >= 2 可登录（超级管理员和普通管理员）
     */
    @Override
    public User loginAsAdmin(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        if (user.getRole() < 2) {
            return null;
        }
        String token = TokenUtil.generate();
        user.setToken(token);
        userMapper.updateToken(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 创建管理员账号（无需邮箱验证）
     */
    @Override
    public User createAdmin(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(null); // 空邮箱设为 null，避免唯一约束冲突
        } else if (userMapper.selectByEmail(user.getEmail()) != null) {
            throw new RuntimeException("该邮箱已被注册");
        }
        user.setRole(2);
        user.setEmailVerified(1);
        user.setRawPassword(user.getPassword()); // 保存明文密码供超级管理员查看
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setToken(TokenUtil.generate());
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 删除管理员（仅超级管理员可操作，不可删除自己，不可删除超级管理员）
     */
    @Override
    public void deleteAdmin(Integer userId, Integer currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new RuntimeException("不能删除自己的管理员账号");
        }
        User target = userMapper.selectById(userId);
        if (target == null) {
            throw new RuntimeException("用户不存在");
        }
        if (target.getRole() >= 3) {
            throw new RuntimeException("不能删除超级管理员");
        }
        userMapper.deleteById(userId);
    }

    /**
     * 列出所有管理员（role >= 2）。仅超级管理员可见密码
     */
    @Override
    public java.util.List<User> listAdmins(Integer currentRole) {
        java.util.List<User> all = userMapper.selectAll();
        java.util.List<User> admins = new java.util.ArrayList<>();
        for (User u : all) {
            if (u.getRole() >= 2) {
                u.setPassword(null); // 始终隐藏哈希密码
                if (currentRole == null || currentRole < 3) {
                    u.setRawPassword(null); // 普通管理员不返回明文密码
                }
                admins.add(u);
            }
        }
        return admins;
    }

    /**
     * 超级管理员修改管理员信息
     */
    @Override
    public void updateAdmin(User user) {
        User target = userMapper.selectById(user.getUserId());
        if (target == null) {
            throw new RuntimeException("用户不存在");
        }
        if (target.getRole() < 2) {
            throw new RuntimeException("只能修改管理员账号");
        }
        // 检查新用户名是否与其他用户冲突
        User exist = userMapper.selectByUsername(user.getUsername());
        if (exist != null && !exist.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("用户名已存在");
        }
        target.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            target.setRawPassword(user.getPassword()); // 更新明文密码
            target.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        target.setPhone(user.getPhone());
        target.setEmail(user.getEmail() != null && !user.getEmail().isBlank() ? user.getEmail() : null);
        userMapper.update(target);
    }

    /**
     * 用户注册：邮箱验证码校验 + 密码加密保存
     */
    @Override
    public User register(User user, String code) {
        // 校验验证码
        if (!verificationStore.verify(user.getEmail(), code)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查邮箱是否已被注册
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            throw new RuntimeException("该邮箱已被注册");
        }
        // 默认角色为普通用户
        if (user.getRole() == null) {
            user.setRole(1);
        }
        // 标记邮箱已验证
        user.setEmailVerified(1);
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 生成令牌
        user.setToken(TokenUtil.generate());
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 查询商城用户列表（仅 role=1，不包含管理员）
     */
    @Override
    public java.util.List<User> list() {
        java.util.List<User> users = userMapper.selectByRole(1);
        users.forEach(u -> u.setPassword(null));
        return users;
    }
}
