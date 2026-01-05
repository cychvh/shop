package com.cyc.xiangwei.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDTO {
    private Order order;
    private List<OrderItem> orderItems;
}
