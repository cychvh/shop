package com.cyc.xiangwei.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyc.xiangwei.bean.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 每日销售额统计 - 仅针对已支付及以上的订单
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, " +
            "COUNT(id) as orderCount, " +
            "SUM(total_amount) as totalAmount " +
            "FROM orders " +
            "WHERE merchant_id = #{merchantId} " +
            "AND status IN (1, 2, 3) " + // 过滤掉取消(0)和退货(4)
            "GROUP BY date " +
            "ORDER BY date DESC LIMIT 15")
    List<Map<String, Object>> getDailySales(Integer merchantId);

    /**
     * 商品销量占比 - 关联 order_item 表
     */
    @Select("SELECT oi.product_name as name, SUM(oi.quantity) as value " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.merchant_id = #{merchantId} " +
            "AND o.status IN (1, 2, 3) " +
            "GROUP BY oi.product_id, oi.product_name " +
            "ORDER BY value DESC LIMIT 10")
    List<Map<String, Object>> getProductPieData(Integer merchantId);
}
