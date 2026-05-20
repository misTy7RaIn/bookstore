package com.bookstore.mapper;

import com.bookstore.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAll();
    User selectById(Integer userId);
    User selectByUsername(String username);
    User selectByEmail(String email);
    User selectByToken(String token);
    List<User> selectByRole(Integer role);
    int updateToken(User user);
    int insert(User user);
    int update(User user);
    int deleteById(Integer userId);
}
