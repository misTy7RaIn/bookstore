package com.bookstore.mapper;

import com.bookstore.pojo.Favorite;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FavoriteMapper {
    List<Favorite> selectByUserId(Integer userId);
    Favorite selectByUserAndBook(Integer userId, Integer bookId);
    int insert(Favorite favorite);
    int deleteById(Integer favoriteId);
    int deleteByBookId(Integer bookId);
}
