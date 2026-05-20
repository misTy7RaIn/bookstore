package com.bookstore.mapper;

import com.bookstore.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BookMapper {
    List<Book> selectAll();
    Book selectById(Integer bookId);
    List<Book> selectByCategory(Integer categoryId);
    List<Book> searchByKeyword(String keyword);
    int insert(Book book);
    int update(Book book);
    int deleteById(Integer bookId);
}
