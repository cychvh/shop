package com.cyc.xiangwei.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DailySalesVO {
    private String date;         // 对应 SQL 中的 date
    private Integer orderCount;  // 对应 SQL 中的 orderCount
    private BigDecimal totalAmount; // 对应 SQL 中的 totalAmount
}
