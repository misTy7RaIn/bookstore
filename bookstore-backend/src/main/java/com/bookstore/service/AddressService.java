package com.bookstore.service;

import com.bookstore.pojo.Address;
import java.util.List;

public interface AddressService {
    List<Address> listByUser(Integer userId);
    Address getDefault(Integer userId);
    void add(Address address);
    void update(Address address);
    void delete(Integer addressId, Integer userId);
    void setDefault(Integer addressId, Integer userId);
}
