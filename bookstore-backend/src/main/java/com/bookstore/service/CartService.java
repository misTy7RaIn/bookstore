package com.bookstore.service;

import com.bookstore.pojo.Cart;
import java.util.List;

public interface CartService {
    List<Cart> listByUser(Integer userId);
    void add(Integer userId, Integer bookId, Integer quantity);
    void updateQuantity(Integer cartId, Integer quantity);
    void remove(Integer cartId);
    void clear(Integer userId);
}
