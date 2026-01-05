package com.cyc.xiangwei.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cart {
    private Integer id;
    private Integer userid;
    private Integer productid;
    private Integer quantity;
}
