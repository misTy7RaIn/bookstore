package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.pojo.Address;
import com.bookstore.pojo.User;
import com.bookstore.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收货地址控制器
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /** 获取当前用户 */
    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute("currentUser");
    }

    /** 地址列表 */
    @GetMapping("/list")
    public Result<List<Address>> list(HttpServletRequest request) {
        List<Address> list = addressService.listByUser(currentUser(request).getUserId());
        return Result.success(list);
    }

    /** 获取默认地址 */
    @GetMapping("/default")
    public Result<Address> getDefault(HttpServletRequest request) {
        Address addr = addressService.getDefault(currentUser(request).getUserId());
        return Result.success(addr);
    }

    /** 添加地址 */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Address address, HttpServletRequest request) {
        address.setUserId(currentUser(request).getUserId());
        addressService.add(address);
        return Result.success();
    }

    /** 修改地址 */
    @PutMapping("/update/{id}")
    public Result<?> update(@PathVariable("id") Integer addressId,
                            @RequestBody Address address, HttpServletRequest request) {
        address.setAddressId(addressId);
        address.setUserId(currentUser(request).getUserId());
        addressService.update(address);
        return Result.success();
    }

    /** 删除地址 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable("id") Integer addressId, HttpServletRequest request) {
        addressService.delete(addressId, currentUser(request).getUserId());
        return Result.success();
    }

    /** 设为默认 */
    @PutMapping("/default/{id}")
    public Result<?> setDefault(@PathVariable("id") Integer addressId, HttpServletRequest request) {
        addressService.setDefault(addressId, currentUser(request).getUserId());
        return Result.success();
    }
}
