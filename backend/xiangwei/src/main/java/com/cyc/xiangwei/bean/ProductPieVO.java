package com.cyc.xiangwei.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductPieVO {
    private String name;   // 对应 SQL 中的 name (商品名称)
    private Integer value; // 对应 SQL 中的 value (销量数量)
}
