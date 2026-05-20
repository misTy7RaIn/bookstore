package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.mapper.DashboardMapper;
import com.bookstore.pojo.DashboardStats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardMapper dashboardMapper;

    public DashboardController(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        DashboardStats stats = dashboardMapper.selectStats();

        Map<String, Object> data = new HashMap<>();
        data.put("bookCount", stats.getBookCount());
        data.put("orderCount", stats.getOrderCount());
        data.put("userCount", stats.getUserCount());
        data.put("totalSales", stats.getTotalSales());
        // 从数据库聚合查询
        data.put("categoryBookCount", dashboardMapper.selectCategoryCount());
        data.put("dailyOrderCount", dashboardMapper.selectDailyOrderCount());

        return Result.success(data);
    }
}
