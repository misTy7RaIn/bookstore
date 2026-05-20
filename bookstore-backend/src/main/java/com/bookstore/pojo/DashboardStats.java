package com.bookstore.pojo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计数据
 */
public class DashboardStats {
    private Integer bookCount;
    private Integer orderCount;
    private Integer userCount;
    private BigDecimal totalSales;
    private List<Map<String, Object>> categoryBookCount;
    private List<Map<String, Object>> dailyOrderCount;

    public Integer getBookCount() { return bookCount; }
    public void setBookCount(Integer bookCount) { this.bookCount = bookCount; }
    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    public Integer getUserCount() { return userCount; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public List<Map<String, Object>> getCategoryBookCount() { return categoryBookCount; }
    public void setCategoryBookCount(List<Map<String, Object>> categoryBookCount) { this.categoryBookCount = categoryBookCount; }
    public List<Map<String, Object>> getDailyOrderCount() { return dailyOrderCount; }
    public void setDailyOrderCount(List<Map<String, Object>> dailyOrderCount) { this.dailyOrderCount = dailyOrderCount; }
}
