package com.bookstore.controller;

import com.bookstore.BookstoreApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户接口测试类
 */
@SpringBootTest(classes = BookstoreApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试登录成功
     */
    @Test
    @Order(1)
    public void testLoginSuccess() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("username", "zhangsan", "password", "123456"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("zhangsan"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    /**
     * 测试登录失败（密码错误）
     */
    @Test
    @Order(2)
    public void testLoginFailWrongPassword() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("username", "zhangsan", "password", "wrong"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    /**
     * 测试登录失败（用户不存在）
     */
    @Test
    @Order(3)
    public void testLoginFailUserNotFound() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("username", "nobody", "password", "123456"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    /**
     * 测试注册成功
     */
    @Test
    @Order(4)
    public void testRegisterSuccess() throws Exception {
        Map<String, String> newUser = Map.of("username", "testuser", "password", "123456", "phone", "13855555555");
        String body = objectMapper.writeValueAsString(newUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    /**
     * 测试注册失败（用户名重复）
     */
    @Test
    @Order(5)
    public void testRegisterFailDuplicate() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("username", "zhangsan", "password", "123456", "phone", "13899999999"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(409));
    }
}
