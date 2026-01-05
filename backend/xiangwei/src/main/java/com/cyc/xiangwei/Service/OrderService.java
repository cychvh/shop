package com.cyc.xiangwei.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.xiangwei.bean.Order;
import com.cyc.xiangwei.bean.OrderItem;
import com.cyc.xiangwei.bean.OrderVo;
import com.cyc.xiangwei.bean.Result;

import java.util.List;

public interface OrderService extends IService<Order> {
     IPage<Order> getOrderByOrderId(String productName, Integer userId, Integer PageNum, Integer PageSize);
     List<OrderItem> getOrderItemsByOrderId(Integer orderId, Integer userId);
     void addOrder(Order order, List<OrderItem> orderItems, Integer userId);
     public IPage<OrderVo> getMerchantOrderList(Integer merchantId, Integer pageNum, Integer pageSize);
     public List<OrderItem> getMerchantOrderItems(Integer merchantId, Integer orderId);
     public void confirmReceiptService(Integer orderId,Integer userId);
}
