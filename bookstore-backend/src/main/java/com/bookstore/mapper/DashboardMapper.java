package com.bookstore.mapper;

import com.bookstore.pojo.DashboardStats;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据聚合查询
 */
@Mapper
public interface DashboardMapper {
    DashboardStats selectStats();
    List<Map<String, Object>> selectCategoryCount();
    List<Map<String, Object>> selectDailyOrderCount();
}
