package com.bookstore.mapper;

import com.bookstore.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    /** 查询所有分类 */
    List<Category> selectAll();
}
