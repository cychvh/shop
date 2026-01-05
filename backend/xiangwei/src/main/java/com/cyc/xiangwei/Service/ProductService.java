package com.cyc.xiangwei.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyc.xiangwei.bean.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface ProductService extends IService<Product> {
    List<Map<String, Object>> getDailySales(Integer merchantId);
    List<Map<String, Object>> getProductPieData(Integer merchantId);
}
