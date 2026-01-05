package com.cyc.xiangwei.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.bean.Order;
import com.cyc.xiangwei.bean.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> findOrderWithItemsByProductName(Page<Order> page,
                                                @Param("userId") Integer userId,
                                                @Param("productName") String productName);
    List<Product> getTopSellingProducts(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
}
