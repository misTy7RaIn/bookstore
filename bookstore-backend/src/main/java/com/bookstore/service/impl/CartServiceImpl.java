package com.bookstore.service.impl;

import com.bookstore.mapper.CartMapper;
import com.bookstore.pojo.Cart;
import com.bookstore.service.CartService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    public CartServiceImpl(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    /**
     * 查询用户购物车列表
     */
    @Override
    public List<Cart> listByUser(Integer userId) {
        return cartMapper.selectByUserId(userId);
    }

    /**
     * 添加商品到购物车（已存在则叠加数量）
     */
    @Override
    public void add(Integer userId, Integer bookId, Integer quantity) {
        Cart exist = cartMapper.selectByUserAndBook(userId, bookId);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + (quantity != null ? quantity : 1));
            cartMapper.updateQuantity(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setBookId(bookId);
            cart.setQuantity(quantity != null ? quantity : 1);
            cartMapper.insert(cart);
        }
    }

    /**
     * 更新商品数量
     */
    @Override
    public void updateQuantity(Integer cartId, Integer quantity) {
        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setQuantity(quantity);
        cartMapper.updateQuantity(cart);
    }

    /**
     * 删除单个商品
     */
    @Override
    public void remove(Integer cartId) {
        cartMapper.deleteById(cartId);
    }

    /**
     * 清空用户购物车
     */
    @Override
    public void clear(Integer userId) {
        cartMapper.deleteByUserId(userId);
    }
}
