package com.bookstore.interceptor;

import com.bookstore.mapper.UserMapper;
import com.bookstore.pojo.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：校验 Authorization 头中的 token，并对管理端接口校验管理员角色
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    /** 需要管理员角色的路径前缀 */
    private static final String[] ADMIN_PATHS = {
        "/api/user/list",
        "/api/dashboard/",
        "/api/order/deliver/",
        "/api/admin/create",
        "/api/admin/delete/",
        "/api/admin/list",
    };

    public LoginInterceptor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            send401(response, "未登录");
            return false;
        }

        User user = userMapper.selectByToken(token);
        if (user == null) {
            send401(response, "令牌无效");
            return false;
        }

        // 管理端接口 —— 仅管理员可访问
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (requiresAdmin(uri, method) && user.getRole() < 2) {
            send403(response);
            return false;
        }

        // 将用户信息写入 request 属性，供 Controller 使用
        request.setAttribute("currentUser", user);
        return true;
    }

    /** 判断是否需要管理员权限 */
    private boolean requiresAdmin(String uri, String method) {
        // 图书 新增/编辑/删除 → 管理员
        if (uri.startsWith("/api/book/") && ("POST".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))) {
            return true;
        }
        if ("/api/book/save".equals(uri)) {
            return true;
        }
        // 其他管理端接口
        for (String path : ADMIN_PATHS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    private void send401(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + msg + "\"}");
    }

    private void send403(HttpServletResponse response) throws Exception {
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"无权限，仅管理员可操作\"}");
    }
}
