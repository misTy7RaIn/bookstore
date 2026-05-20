package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.pojo.Cart;
import com.bookstore.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestParam Integer userId) {
        List<Cart> list = cartService.listByUser(userId);
        return Result.success(list);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<?> add(@RequestParam Integer userId,
                         @RequestParam Integer bookId,
                         @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.add(userId, bookId, quantity);
        return Result.success();
    }

    /**
     * 更新数量
     */
    @PutMapping("/update")
    public Result<?> update(@RequestParam Integer cartId,
                            @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/remove/{id}")
    public Result<?> remove(@PathVariable("id") Integer cartId) {
        cartService.remove(cartId);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<?> clear(@RequestParam Integer userId) {
        cartService.clear(userId);
        return Result.success();
    }
}
