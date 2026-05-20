package com.bookstore.service;

import com.bookstore.pojo.User;

public interface UserService {
    User login(String username, String password);
    User register(User user, String code);
    java.util.List<User> list();

    /** 管理员登录（role >= 2 可登录） */
    User loginAsAdmin(String username, String password);
    /** 创建管理员账号（仅超级管理员） */
    User createAdmin(User user);
    /** 删除管理员（仅超级管理员，不可删除自己） */
    void deleteAdmin(Integer userId, Integer currentAdminId);
    /** 列出所有管理员（role >= 2），currentRole 为调用者角色 */
    java.util.List<User> listAdmins(Integer currentRole);
    /** 超级管理员修改管理员信息 */
    void updateAdmin(User user);
}
