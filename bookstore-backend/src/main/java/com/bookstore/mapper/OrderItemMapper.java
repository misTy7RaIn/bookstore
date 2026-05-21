package com.bookstore.mapper;

import com.bookstore.pojo.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 订单明细映射接口
 */
@Mapper
public interface OrderItemMapper {
    List<OrderItem> selectByOrderId(String orderId);
    int insert(OrderItem item);
    int deleteByBookId(Integer bookId);
    int deleteByOrderId(String orderId);
}
