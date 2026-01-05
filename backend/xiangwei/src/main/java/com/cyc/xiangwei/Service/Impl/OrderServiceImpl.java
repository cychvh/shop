package com.cyc.xiangwei.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.xiangwei.Service.DeliveryService;
import com.cyc.xiangwei.Service.OrderService;
import com.cyc.xiangwei.Service.ProductService;
import com.cyc.xiangwei.Service.UserService;
import com.cyc.xiangwei.bean.*;
import com.cyc.xiangwei.mapper.CartMapper;
import com.cyc.xiangwei.mapper.OrderItemMapper;
import com.cyc.xiangwei.mapper.OrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;

    @Override
    public IPage<Order> getOrderByOrderId(String productName, Integer userId, Integer pageNum, Integer pageSize) {
        // 使用 MyBatis-Plus 自动分页
        Page<Order> page = new Page<>(pageNum, pageSize);
        List<Order> orders = baseMapper.findOrderWithItemsByProductName(page, userId, productName);
        page.setRecords(orders);  // MyBatis-Plus 会自动填充 LIMIT
        return page;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId, Integer userId) {
        Order order = baseMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId) ) {
            throw new RuntimeException("订单不存在或无权限");
        }
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrder(Order orderParam, List<OrderItem> orderItems, Integer userId) {

        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("订单商品不能为空");
        }

        // 1. 按 merchantId 分组
        HashMap<Integer, List<OrderItem>> merchantMap = new HashMap<>();

        for (OrderItem item : orderItems) {
            Product dbProduct = productService.getById(item.getProductId());
            if (dbProduct == null) {
                throw new RuntimeException("商品不存在：" + item.getProductId());
            }

            if (dbProduct.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品【" + dbProduct.getName() + "】库存不足");
            }

            // 扣库存
            dbProduct.setStock(dbProduct.getStock() - item.getQuantity());
            productService.updateById(dbProduct);

            // 设置订单项真实数据
            item.setProductName(dbProduct.getName());
            item.setPrice(dbProduct.getPrice());

            if (merchantMap.containsKey(dbProduct.getMerchantId())) {
                merchantMap.get(dbProduct.getMerchantId()).add(item);
            } else {
                List<OrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(item);
            }
        }

        // 2. 每个商家生成一个订单
        for (Integer merchantId : merchantMap.keySet()) {

            List<OrderItem> items = merchantMap.get(merchantId);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : items) {
                totalAmount = totalAmount.add(
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }

            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setMerchantId(merchantId);
            order.setTotalAmount(totalAmount);
            order.setStatus(1);
            order.setCreateTime(LocalDateTime.now());

            baseMapper.insert(order); // ★ order.id 在这里生成

            // 插入订单项
            for (OrderItem item : items) {
                item.setOrderId(order.getId());
                orderItemMapper.insert(item);
            }
        }

        // 3. 清空购物车（按用户）
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserid, userId));
    }




    @Override
    public IPage<OrderVo> getMerchantOrderList(Integer merchantId, Integer pageNum, Integer pageSize) {
        // 查询商家订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMerchantId, merchantId)
                .orderByDesc(Order::getCreateTime);

        Page<Order> page = this.page(new Page<>(pageNum, pageSize), wrapper);

        // 转换成 OrderVo 列表
        List<OrderVo> orderVos = new ArrayList<>();
        for (Order record : page.getRecords()) {
            OrderVo orderVo = new OrderVo();
            // 拷贝 Order 的字段
            BeanUtils.copyProperties(record, orderVo);

            // 获取用户信息
            User dbUser = userService.getById(record.getUserId());
            if (dbUser != null) {
                orderVo.setUsername(dbUser.getUsername());
                orderVo.setAddress(dbUser.getAddress());
                orderVo.setPhone(dbUser.getPhone());
            }

            orderVos.add(orderVo);
        }

        // 构造分页返回
        Page<OrderVo> orderVoPage = new Page<>();
        BeanUtils.copyProperties(page, orderVoPage); // 拷贝分页信息
        orderVoPage.setRecords(orderVos);            // 设置新的记录列表

        return orderVoPage;
    }

    @Override
    public List<OrderItem> getMerchantOrderItems(Integer merchantId, Integer orderId) {
        Order order = baseMapper.selectById(orderId);
        if (order == null || !order.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("订单不存在或无权查看");
        }

        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //确认发货
    public void confirmReceiptService(Integer orderId, Integer userId) {
        Order order = baseMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在或无权限");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不允许确认收货");
        }
        order.setStatus(3);
        // 3. 查询当前有效的物流（只允许已发货状态）
        OrderDelivery delivery = deliveryService.getOne(
                new LambdaQueryWrapper<OrderDelivery>()
                        .eq(OrderDelivery::getOrderId, orderId)
                        .eq(OrderDelivery::getStatus, 1) // 已发货
                        .last("LIMIT 1")
        );
        if (delivery == null) {
            throw new RuntimeException("未找到可确认的物流信息");
        }
        delivery.setStatus(2);
        deliveryService.updateById(delivery);
        order.setStatus(3);
        baseMapper.updateById(order);
    }


}


