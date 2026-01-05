package com.cyc.xiangwei.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    //物品的种类名
    private String categoryname;
    private BigDecimal price;
    //库存
    private Integer stock;
    //产地的地址
    private String originplace;
    //图片的地址
    private String imageurl;
    //状态
    private Integer status;
    @TableField("MerchantId")
    private Integer MerchantId;



}
