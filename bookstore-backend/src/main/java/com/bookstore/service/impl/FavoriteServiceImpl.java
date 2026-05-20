package com.bookstore.service.impl;

import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.pojo.Favorite;
import com.bookstore.service.FavoriteService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * 查询用户收藏列表
     */
    @Override
    public List<Favorite> listByUser(Integer userId) {
        return favoriteMapper.selectByUserId(userId);
    }

    /**
     * 添加收藏（已存在则忽略）
     */
    @Override
    public void add(Integer userId, Integer bookId) {
        Favorite exist = favoriteMapper.selectByUserAndBook(userId, bookId);
        if (exist != null) return;
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setBookId(bookId);
        favoriteMapper.insert(f);
    }

    /**
     * 取消收藏
     */
    @Override
    public void remove(Integer favoriteId) {
        favoriteMapper.deleteById(favoriteId);
    }
}
