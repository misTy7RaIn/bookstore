package com.bookstore.service;

import com.bookstore.pojo.Order;
import java.util.List;

public interface OrderService {
    List<Order> listByUser(Integer userId);
    Order detail(String orderId);
    String create(Order order);
    void pay(String orderId);
    void deliver(String orderId);
    void receive(String orderId);
    void cancel(String orderId);
}
