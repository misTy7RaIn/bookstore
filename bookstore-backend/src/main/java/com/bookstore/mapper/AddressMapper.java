package com.bookstore.mapper;

import com.bookstore.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AddressMapper {
    List<Address> selectByUserId(Integer userId);
    Address selectDefault(Integer userId);
    int insert(Address address);
    int update(Address address);
    int deleteById(Integer addressId);
    int clearDefault(Integer userId);
}
