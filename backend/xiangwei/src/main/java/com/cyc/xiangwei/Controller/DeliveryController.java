package com.cyc.xiangwei.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.Service.DeliveryService;
import com.cyc.xiangwei.bean.OrderDelivery;
import com.cyc.xiangwei.bean.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/ship")
    public Result<?> ship(@RequestBody OrderDelivery orderDelivery,
                          HttpServletRequest request) {

        Integer merchantId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (merchantId == null || type == null || type != 1) {
            return Result.error("403", "权限不足");
        }

        deliveryService.shipOrder(
                orderDelivery.getOrderId(),
                merchantId,
                orderDelivery.getExpressCompany(),
                orderDelivery.getExpressNo()
        );

        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(required = false) Integer orderId,
                          HttpServletRequest request) {
        System.out.println("list被调用");

        Integer merchantId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (merchantId == null || type == null || type != 1) {
            return Result.error("403", "权限不足");
        }

        LambdaQueryWrapper<OrderDelivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDelivery::getMerchantId, merchantId);

        if (orderId != null) {
            wrapper.eq(OrderDelivery::getOrderId, orderId);
        }

        wrapper.orderByDesc(OrderDelivery::getShipTime);

        Page<OrderDelivery> page = deliveryService.page(new Page<>(pageNum, pageSize), wrapper);

        return Result.success(page);
    }
    @PutMapping("/correct")
    public Result<?> correctDelivery(@RequestBody OrderDelivery orderDelivery,
                                     HttpServletRequest request) {

        Integer merchantId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (merchantId == null || type == null || type != 1) {
            return Result.error("403", "权限不足");
        }

        // 基本参数校验（只校验“有没有”）
        if (orderDelivery.getOrderId() == null
                || !StringUtils.hasText(orderDelivery.getExpressCompany())
                || !StringUtils.hasText(orderDelivery.getExpressNo())) {
            return Result.error("400", "物流信息不完整");
        }

        // 强制使用当前商户
        orderDelivery.setMerchantId(merchantId);

        // 核心业务全部交给 Service
        deliveryService.addOrUpdateDelivery(orderDelivery);

        return Result.success();
    }

    @GetMapping("/getOne")
    public Result<?> getOne(@RequestParam Integer orderId,HttpServletRequest servletRequest) {
        Integer type = (Integer) servletRequest.getAttribute("type");
        if (type == null || type != 2) {
            return Result.error("403", "权限不足");
        }
        OrderDelivery delivery = deliveryService.getDelivery(orderId);
        return Result.success(delivery);
    }



}
