package com.bookstore.service;

import com.bookstore.pojo.Book;
import java.util.List;

public interface BookService {
    List<Book> list(Integer categoryId);
    Book detail(Integer bookId);
    List<Book> search(String keyword);
    void save(Book book);
    void delete(Integer bookId);
}
