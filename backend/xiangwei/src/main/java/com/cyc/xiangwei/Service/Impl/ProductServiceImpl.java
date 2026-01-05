package com.cyc.xiangwei.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.xiangwei.Service.ProductService;
import com.cyc.xiangwei.bean.Product;
import com.cyc.xiangwei.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Override
    public List<Map<String, Object>> getDailySales(Integer merchantId) {
        return baseMapper.getDailySales(merchantId);
    }

    @Override
    public List<Map<String, Object>> getProductPieData(Integer merchantId) {
        return baseMapper.getProductPieData(merchantId);
    }
}
