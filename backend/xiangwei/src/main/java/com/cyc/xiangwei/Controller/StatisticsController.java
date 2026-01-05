package com.cyc.xiangwei.Controller;

import com.cyc.xiangwei.Service.StatisticsService;
import com.cyc.xiangwei.bean.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/all")
    public Result<?> getAllStats(HttpServletRequest request) {
        Integer merchantId = (Integer) request.getAttribute("userId");
        if (merchantId == null) return Result.error("403", "未登录");

        // 返回一个包含两个列表的复合结果
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("daily", statisticsService.getDailySales(merchantId));
        map.put("pie", statisticsService.getProductPieData(merchantId));

        return Result.success(map);
    }
}
