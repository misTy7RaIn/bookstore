package com.bookstore.mapper;

import com.bookstore.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> selectAll();
    List<Order> selectByUserId(Integer userId);
    Order selectById(String orderId);
    int insert(Order order);
    int updateStatus(Order order);
}
