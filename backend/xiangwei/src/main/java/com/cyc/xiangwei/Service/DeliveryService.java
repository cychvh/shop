package com.cyc.xiangwei.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.xiangwei.bean.OrderDelivery;

public interface DeliveryService extends IService<OrderDelivery> {
    public void shipOrder(Integer orderId, Integer merchantId, String expressCompany, String expressNo);
    public void addOrUpdateDelivery(OrderDelivery newDelivery);
    public OrderDelivery getDelivery(Integer orderId);
}
