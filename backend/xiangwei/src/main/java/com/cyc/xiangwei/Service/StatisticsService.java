package com.cyc.xiangwei.Service;

import com.cyc.xiangwei.bean.DailySalesVO;
import com.cyc.xiangwei.bean.ProductPieVO;

import java.util.List;

public interface StatisticsService {
    List<DailySalesVO> getDailySales(Integer merchantId);
    List<ProductPieVO> getProductPieData(Integer merchantId);
}
