package com.bookstore.service.impl;

import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.pojo.Order;
import com.bookstore.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * 查询全部订单（管理端）
     */
    @Override
    public List<Order> listByUser(Integer userId) {
        if (userId != null && userId > 0) {
            return orderMapper.selectByUserId(userId);
        }
        return orderMapper.selectAll();
    }

    /**
     * 查询订单详情
     */
    @Override
    public Order detail(String orderId) {
        return orderMapper.selectById(orderId);
    }

    /**
     * 创建订单
     */
    @Override
    public String create(Order order) {
        // 若已有订单号则保留（结算时传入）
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            order.setOrderId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        // 若调用方已设置状态（如结算时直接设为待发货），则保留；否则默认待发货
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(1);
        }
        orderMapper.insert(order);
        return order.getOrderId();
    }

    /**
     * 支付
     */
    @Override
    public void pay(String orderId) {
        Order o = new Order();
        o.setOrderId(orderId);
        o.setOrderStatus(1);
        o.setPayTime(LocalDateTime.now());
        orderMapper.updateStatus(o);
    }

    /**
     * 发货（管理端操作）
     */
    @Override
    public void deliver(String orderId) {
        Order o = new Order();
        o.setOrderId(orderId);
        o.setOrderStatus(2);
        o.setDeliverTime(LocalDateTime.now());
        orderMapper.updateStatus(o);
    }

    /**
     * 收货
     */
    @Override
    public void receive(String orderId) {
        Order o = new Order();
        o.setOrderId(orderId);
        o.setOrderStatus(3);
        o.setReceiveTime(LocalDateTime.now());
        orderMapper.updateStatus(o);
    }

    /**
     * 取消
     */
    @Override
    public void cancel(String orderId) {
        Order o = new Order();
        o.setOrderId(orderId);
        o.setOrderStatus(4);
        o.setCancelTime(LocalDateTime.now());
        orderMapper.updateStatus(o);
    }

    /**
     * 删除订单（先删明细，再删主表）
     */
    @Override
    public void delete(String orderId) {
        orderItemMapper.deleteByOrderId(orderId);
        orderMapper.deleteById(orderId);
    }
}
