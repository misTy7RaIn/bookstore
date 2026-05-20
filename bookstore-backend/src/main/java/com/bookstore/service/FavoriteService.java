package com.bookstore.service;

import com.bookstore.pojo.Favorite;
import java.util.List;

public interface FavoriteService {
    List<Favorite> listByUser(Integer userId);
    void add(Integer userId, Integer bookId);
    void remove(Integer favoriteId);
}
