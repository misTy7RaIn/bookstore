package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.mapper.CartMapper;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.pojo.Book;
import com.bookstore.pojo.Cart;
import com.bookstore.pojo.Order;
import com.bookstore.pojo.OrderItem;
import com.bookstore.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;

    public OrderController(OrderService orderService, OrderItemMapper orderItemMapper,
                           CartMapper cartMapper, BookMapper bookMapper) {
        this.orderService = orderService;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.bookMapper = bookMapper;
    }

    /**
     * 订单列表（含书籍名称）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) Integer userId) {
        List<Order> list = orderService.listByUser(userId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Order o : list) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(o.getOrderId());
            // 提取书籍名称，多本用顿号连接
            String bookNames = items.stream()
                    .map(OrderItem::getBookName)
                    .reduce((a, b) -> a + "、" + b)
                    .orElse("未知");
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", o.getOrderId());
            map.put("userId", o.getUserId());
            map.put("totalPrice", o.getTotalPrice());
            map.put("receiverName", o.getReceiverName());
            map.put("receiverPhone", o.getReceiverPhone());
            map.put("receiverAddress", o.getReceiverAddress());
            map.put("orderStatus", o.getOrderStatus());
            map.put("payTime", o.getPayTime());
            map.put("deliverTime", o.getDeliverTime());
            map.put("receiveTime", o.getReceiveTime());
            map.put("cancelTime", o.getCancelTime());
            map.put("createTime", o.getCreateTime());
            map.put("bookNames", bookNames);
            map.put("items", items);
            result.add(map);
        }
        return Result.success(result);
    }

    /**
     * 订单详情（含明细）
     */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable("id") String orderId) {
        Order order = orderService.detail(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        return Result.success(data);
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody Order order) {
        String orderId = orderService.create(order);
        return Result.success(orderId);
    }

    /**
     * 结算：购物车选中商品 → 创建订单 → 清空已购买购物车项（事务保证一致性）
     */
    @Transactional
    @PostMapping("/checkout")
    public Result<String> checkout(@RequestBody Map<String, Object> body) {
        Integer userId = (Integer) body.get("userId");
        String receiverName = (String) body.get("receiverName");
        String receiverPhone = (String) body.get("receiverPhone");
        String receiverAddress = (String) body.get("receiverAddress");
        @SuppressWarnings("unchecked")
        List<Integer> cartIds = (List<Integer>) body.get("cartIds");

        if (cartIds == null || cartIds.isEmpty()) {
            return Result.error(400, "请选择要结算的商品");
        }

        // 生成订单号并先创建订单（外键约束要求 orders 先存在）
        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 先计算总价
        for (Integer cartId : cartIds) {
            Cart cart = cartMapper.selectByUserId(userId).stream()
                    .filter(c -> c.getCartId().equals(cartId))
                    .findFirst().orElse(null);
            if (cart == null) continue;
            Book book = bookMapper.selectById(cart.getBookId());
            if (book == null) continue;
            totalPrice = totalPrice.add(book.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        // 创建订单记录（下单即支付，状态=待发货）
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setOrderStatus(1); // 待发货
        order.setPayTime(java.time.LocalDateTime.now());
        orderService.create(order);

        // 再插入订单明细，并从购物车移除
        for (Integer cartId : cartIds) {
            Cart cart = cartMapper.selectByUserId(userId).stream()
                    .filter(c -> c.getCartId().equals(cartId))
                    .findFirst().orElse(null);
            if (cart == null) continue;

            Book book = bookMapper.selectById(cart.getBookId());
            if (book == null) continue;

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setBookId(book.getBookId());
            item.setBookName(book.getBookName());
            item.setPrice(book.getPrice());
            item.setQuantity(cart.getQuantity());
            orderItemMapper.insert(item);

            cartMapper.deleteById(cartId);
        }

        return Result.success(orderId);
    }

    /**
     * 支付
     */
    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable("id") String orderId) {
        orderService.pay(orderId);
        return Result.success();
    }

    /**
     * 发货（管理端）
     */
    @PutMapping("/deliver/{id}")
    public Result<?> deliver(@PathVariable("id") String orderId) {
        orderService.deliver(orderId);
        return Result.success();
    }

    /**
     * 收货
     */
    @PutMapping("/receive/{id}")
    public Result<?> receive(@PathVariable("id") String orderId) {
        orderService.receive(orderId);
        return Result.success();
    }

    /**
     * 取消
     */
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable("id") String orderId) {
        orderService.cancel(orderId);
        return Result.success();
    }
}
