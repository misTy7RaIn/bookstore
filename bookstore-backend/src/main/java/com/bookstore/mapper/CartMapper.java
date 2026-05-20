package com.bookstore.mapper;

import com.bookstore.pojo.Cart;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CartMapper {
    List<Cart> selectByUserId(Integer userId);
    Cart selectByUserAndBook(Integer userId, Integer bookId);
    int insert(Cart cart);
    int updateQuantity(Cart cart);
    int deleteById(Integer cartId);
    int deleteByUserId(Integer userId);
    int deleteByBookId(Integer bookId);
}
