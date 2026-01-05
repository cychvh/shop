package com.cyc.xiangwei.mapper;

import com.cyc.xiangwei.bean.DailySalesVO;
import com.cyc.xiangwei.bean.ProductPieVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface StatisticsMapper {
    // 统计商家每日销售额（状态 1,2,3）
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, " +
            "COUNT(id) as orderCount, SUM(total_amount) as totalAmount " +
            "FROM orders " +
            "WHERE merchant_id = #{merchantId} AND status IN (1, 2, 3) " +
            "GROUP BY date ORDER BY date DESC LIMIT 15")
    List<DailySalesVO> getDailySales(Integer merchantId);

    // 统计商家商品销量排行
    @Select("SELECT oi.product_name as name, SUM(oi.quantity) as value " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.merchant_id = #{merchantId} AND o.status IN (1, 2, 3) " +
            "GROUP BY oi.product_id, oi.product_name " +
            "ORDER BY value DESC LIMIT 10")
    List<ProductPieVO> getProductPieData(Integer merchantId);
}
