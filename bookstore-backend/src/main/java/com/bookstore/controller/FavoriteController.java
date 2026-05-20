package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.pojo.Favorite;
import com.bookstore.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏列表
     */
    @GetMapping("/list")
    public Result<List<Favorite>> list(@RequestParam Integer userId) {
        List<Favorite> list = favoriteService.listByUser(userId);
        return Result.success(list);
    }

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<?> add(@RequestParam Integer userId,
                         @RequestParam Integer bookId) {
        favoriteService.add(userId, bookId);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove/{id}")
    public Result<?> remove(@PathVariable("id") Integer favoriteId) {
        favoriteService.remove(favoriteId);
        return Result.success();
    }
}
