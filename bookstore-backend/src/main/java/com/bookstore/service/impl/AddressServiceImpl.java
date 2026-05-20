package com.bookstore.service.impl;

import com.bookstore.mapper.AddressMapper;
import com.bookstore.pojo.Address;
import com.bookstore.service.AddressService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<Address> listByUser(Integer userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public Address getDefault(Integer userId) {
        return addressMapper.selectDefault(userId);
    }

    @Override
    public void add(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(address.getUserId());
        }
        addressMapper.insert(address);
    }

    @Override
    public void update(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(address.getUserId());
        }
        addressMapper.update(address);
    }

    @Override
    public void delete(Integer addressId, Integer userId) {
        addressMapper.deleteById(addressId);
    }

    @Override
    public void setDefault(Integer addressId, Integer userId) {
        addressMapper.clearDefault(userId);
        Address addr = new Address();
        addr.setAddressId(addressId);
        addr.setIsDefault(1);
        addressMapper.update(addr);
    }
}
