package com.cyc.xiangwei.bean;

import lombok.Data;

@Data
public class OrderVo extends Order {
    private String username;
    private String address;
    private String phone;
}
