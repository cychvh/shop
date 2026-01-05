package com.cyc.xiangwei.bean;

import lombok.Data;

import java.math.BigDecimal;

// CartVo.java
@Data
public class CartVo extends Cart {
    // 继承 Cart 的原有属性
    private String productName;
    private BigDecimal price;
    private String imageurl;
}