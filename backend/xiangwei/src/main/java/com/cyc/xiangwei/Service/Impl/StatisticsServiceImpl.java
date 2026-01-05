package com.cyc.xiangwei.Service.Impl;

import com.cyc.xiangwei.Service.StatisticsService;
import com.cyc.xiangwei.bean.DailySalesVO;
import com.cyc.xiangwei.bean.ProductPieVO;
import com.cyc.xiangwei.mapper.StatisticsMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Resource
    private StatisticsMapper statisticsMapper;
    @Override
    public List<DailySalesVO> getDailySales(Integer merchantId) {
        return statisticsMapper.getDailySales(merchantId);
    }

    @Override
    public List<ProductPieVO> getProductPieData(Integer merchantId) {
        return statisticsMapper.getProductPieData(merchantId);
    }
}
