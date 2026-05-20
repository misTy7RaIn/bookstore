package com.bookstore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 根路径：后端服务状态提示
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "<html><head><meta charset='utf-8'><title>BookStore</title><style>" +
               "body{display:flex;align-items:center;justify-content:center;height:100vh;" +
               "margin:0;font-family:Arial,sans-serif;background:#f5f7fa;}" +
               "h1{color:#2c3e50;font-size:24px;}" +
               "</style></head><body><h1>后端服务已启动 ✅</h1></body></html>";
    }
}
