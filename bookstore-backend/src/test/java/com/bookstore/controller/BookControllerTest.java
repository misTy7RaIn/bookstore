package com.bookstore.controller;

import com.bookstore.BookstoreApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 图书查询接口测试类
 */
@SpringBootTest(classes = BookstoreApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试查询全部图书列表
     */
    @Test
    @Order(1)
    public void testListAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(10));
    }

    /**
     * 测试按分类筛选图书列表（categoryId=9 计算机类）
     */
    @Test
    @Order(2)
    public void testListByCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/list")
                        .param("categoryId", "9")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    /**
     * 测试查询图书详情
     */
    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/detail/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.bookName").value("活着"));
    }

    /**
     * 测试查询不存在的图书返回404
     */
    @Test
    @Order(4)
    public void testDetailNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/detail/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 测试关键字搜索
     */
    @Test
    @Order(5)
    public void testSearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/search")
                        .param("keyword", "三体")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].bookName").value("三体"));
    }
}
