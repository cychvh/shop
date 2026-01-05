package com.cyc.xiangwei.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.Service.OrderService;
import com.cyc.xiangwei.Service.ProductService;
import com.cyc.xiangwei.bean.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;


    @GetMapping("/userList")
    public Result<?> userList(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam(defaultValue = "") String productName,
                              HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");
        if (userId == null || type == null || type != 2) {
            return Result.error("405", "权限不足");
        }
        IPage<Order> orderByOrderId = orderService.getOrderByOrderId(productName, userId, pageNum, pageSize);
        return Result.success(orderByOrderId);
    }

    @GetMapping("/adminList")
    public Result<?> adminList(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "5") Integer pageSize,
                               HttpServletRequest request,
                               @RequestParam Integer orderId) {
        Integer type = (Integer) request.getAttribute("type");
        if (type == null || type != 0) {
            return Result.error("405", "权限不足");
        }
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (orderId != null) {
            orderLambdaQueryWrapper.eq(Order::getId, orderId);
        }
        Page<Order> page = orderService.page(new Page<>(pageNum, pageSize), orderLambdaQueryWrapper);
        return Result.success(page);
    }

    @GetMapping("/getOrderItem")
    public Result<?> getOrderItem(@RequestParam Integer orderId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<OrderItem> orderItemsByOrderId = null;
        try {
            orderItemsByOrderId = orderService.getOrderItemsByOrderId(orderId, userId);
            return Result.success(orderItemsByOrderId);
        } catch (Exception e) {
            return Result.error("405", e.getMessage());
        }

    }

    @PostMapping("/addOrder")
    public Result<?> addOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        System.out.println("addOrder被调用");
        Integer userId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");
        if (userId == null || type == null || type != 2) {
            return Result.error("405", "权限不足");
        }
        if (orderDTO == null) {
            return Result.error("405", "添加的数据为空");
        }
        Order order = orderDTO.getOrder();
        //System.out.println(order);
        List<OrderItem> orderItems = orderDTO.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            System.out.println("Controller中的orderItem" + orderItem);
        }

        try {
            orderService.addOrder(order, orderItems, userId);

            return Result.success();
        } catch (Exception e) {
            return Result.error("405", e.getMessage());
        }
    }



    @GetMapping("/MerchantList")
    public Result<?> merchantList(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  HttpServletRequest request) {

        Integer merchantId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (merchantId == null || type == null || type != 1) {
            return Result.error("405", "权限不足");
        }

        return Result.success(
                orderService.getMerchantOrderList(merchantId, pageNum, pageSize)
        );
    }


    @GetMapping("/merchantOrderItems")
    public Result<?> getMerchantOrderItems(@RequestParam Integer orderId, HttpServletRequest request) {
        Integer merchantId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (merchantId == null || type == null || type != 1) {
            return Result.error("403", "权限不足");
        }

        try {
            List<OrderItem> items = orderService.getMerchantOrderItems(merchantId, orderId);
            return Result.success(items);
        } catch (RuntimeException e) {
            return Result.error("404", e.getMessage());
        }
    }

    @PutMapping("/confirmReceipt")
    public Result<?> confirmReceipt(@RequestParam Integer orderId,
                                    HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        // 1. 权限校验
        if (userId == null || type == null || type != 2) {
            return Result.error("403", "权限不足");
        }

        // 2. 参数校验（只校验“有没有”）
        if (orderId == null) {
            return Result.error("400", "订单ID不能为空");
        }

        // 3. 核心业务
        orderService.confirmReceiptService(orderId, userId);

        return Result.success("确认收货成功");
    }

}
