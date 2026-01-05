package com.cyc.xiangwei.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@TableName("orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer merchantId;   // ★ 必须有

    private BigDecimal totalAmount;

    private Integer status;

    private LocalDateTime createTime;
}
