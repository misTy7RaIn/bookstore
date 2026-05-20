package com.bookstore.service.impl;

import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CartMapper;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.pojo.Book;
import com.bookstore.service.BookService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final CartMapper cartMapper;
    private final FavoriteMapper favoriteMapper;
    private final OrderItemMapper orderItemMapper;

    public BookServiceImpl(BookMapper bookMapper, CartMapper cartMapper,
                           FavoriteMapper favoriteMapper, OrderItemMapper orderItemMapper) {
        this.bookMapper = bookMapper;
        this.cartMapper = cartMapper;
        this.favoriteMapper = favoriteMapper;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * 查询图书列表（可选的分类筛选）
     */
    @Override
    public List<Book> list(Integer categoryId) {
        if (categoryId != null && categoryId > 0) {
            return bookMapper.selectByCategory(categoryId);
        }
        return bookMapper.selectAll();
    }

    /**
     * 查询图书详情
     */
    @Override
    public Book detail(Integer bookId) {
        return bookMapper.selectById(bookId);
    }

    /**
     * 关键字模糊搜索
     */
    @Override
    public List<Book> search(String keyword) {
        return bookMapper.searchByKeyword(keyword);
    }

    /**
     * 新增或编辑图书（有 bookId 则更新，无则插入）
     */
    @Override
    public void save(Book book) {
        if (book.getBookId() != null && book.getBookId() > 0) {
            bookMapper.update(book);
        } else {
            bookMapper.insert(book);
        }
    }

    /**
     * 删除图书（先删关联数据：购物车、收藏、订单明细，再删图书本身）
     */
    @Override
    public void delete(Integer bookId) {
        cartMapper.deleteByBookId(bookId);
        favoriteMapper.deleteByBookId(bookId);
        orderItemMapper.deleteByBookId(bookId);
        bookMapper.deleteById(bookId);
    }
}
