package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.pojo.Book;
import com.bookstore.pojo.Category;
import com.bookstore.service.BookService;
import com.bookstore.util.OssService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;
    private final CategoryMapper categoryMapper;
    private final OssService ossService;

    public BookController(BookService bookService, CategoryMapper categoryMapper,
                          OssService ossService) {
        this.bookService = bookService;
        this.categoryMapper = categoryMapper;
        this.ossService = ossService;
    }

    /**
     * 图书列表（支持按分类筛选）
     */
    @GetMapping("/list")
    public Result<List<Book>> list(@RequestParam(required = false) Integer categoryId) {
        List<Book> list = bookService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 图书详情
     */
    @GetMapping("/detail/{id}")
    public Result<Book> detail(@PathVariable("id") Integer bookId) {
        Book book = bookService.detail(bookId);
        if (book == null) {
            return Result.error(404, "图书不存在");
        }
        return Result.success(book);
    }

    /**
     * 关键字搜索
     */
    @GetMapping("/search")
    public Result<List<Book>> search(@RequestParam String keyword) {
        List<Book> list = bookService.search(keyword);
        return Result.success(list);
    }

    /**
     * 分类列表
     */
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        List<Category> list = categoryMapper.selectAll();
        return Result.success(list);
    }

    /**
     * 新增或编辑图书（支持封面上传）
     */
    @PostMapping("/save")
    public Result<?> save(@RequestParam(required = false) String bookName,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) Integer categoryId,
                          @RequestParam(required = false) String price,
                          @RequestParam(required = false) Integer stock,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) Integer bookId,
                          @RequestParam(required = false) MultipartFile coverFile,
                          HttpServletRequest request) throws IOException {

        // 调试日志：HTTP 方法和请求头
        System.out.println("[DEBUG] method=" + request.getMethod() + " ContentType=" + request.getContentType());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println("[DEBUG] Header: " + name + "=" + request.getHeader(name));
        }
        System.out.println("[DEBUG save] bookName=[" + bookName + "] author=[" + author
                + "] categoryId=[" + categoryId + "] price=[" + price + "] stock=[" + stock
                + "] status=[" + status + "] bookId=[" + bookId
                + "] coverFile=[" + (coverFile != null ? coverFile.getOriginalFilename() : "null") + "]");

        // 必填字段校验
        if (bookName == null || bookName.isBlank()) return Result.error(400, "书名不能为空");
        if (author == null || author.isBlank()) return Result.error(400, "作者不能为空");
        if (categoryId == null) return Result.error(400, "分类不能为空");
        if (price == null || price.isBlank()) return Result.error(400, "价格不能为空");
        if (stock == null) return Result.error(400, "库存不能为空");
        if (status == null) return Result.error(400, "状态不能为空");

        Book book = new Book();
        if (bookId != null && bookId > 0) {
            book.setBookId(bookId);
        }
        book.setBookName(bookName);
        book.setAuthor(author);
        book.setCategoryId(categoryId);
        book.setStock(stock);
        book.setDescription(description);
        if (status != null) book.setStatus(status); else book.setStatus(1);

        // 价格转换
        if (price != null && !price.isEmpty()) {
            book.setPrice(new java.math.BigDecimal(price));
        }

        // 封面图：上传到 OSS；编辑且未传新图时保留原图
        if (coverFile != null && !coverFile.isEmpty()) {
            String ossUrl = ossService.upload(coverFile);
            book.setCoverImg(ossUrl);
        } else if (book.getBookId() != null) {
            Book old = bookService.detail(book.getBookId());
            if (old != null) book.setCoverImg(old.getCoverImg());
        }

        bookService.save(book);
        return Result.success();
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") Integer bookId) {
        bookService.delete(bookId);
        return Result.success();
    }
}
