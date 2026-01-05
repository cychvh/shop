package com.cyc.xiangwei.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.Service.ProductService;
import com.cyc.xiangwei.bean.Product;
import com.cyc.xiangwei.bean.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 商品控制层（终稿安全版）
 */
@RestController
@RequestMapping("/product")
public class productController {

    @Autowired
    private ProductService productService;

    /** 项目根目录 */
    private static final String ROOT_DIR = "D:/myapp";
    /** 商品图片目录 */
    private static final String PRODUCT_IMG_DIR = ROOT_DIR + "/uploads/product/";

    /**
     * 商家商品列表（只看自己的商品）
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "5") int pageSize,
                          @RequestParam(defaultValue = "") String search,
                          HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (userId == null) {
            return Result.error("401", "未登录");
        }
        if (type != 1) {
            return Result.error("403", "非商家无权访问");
        }

        LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();
        query.eq(Product::getMerchantId, userId);

        if (StringUtils.hasText(search)) {
            query.like(Product::getName, search);
        }

        Page<Product> page = productService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }


    /**
     * 用户商品列表（只显示上架商品）
     */
    @GetMapping("/user/list")
    public Result<?> listForUser(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "8") int pageSize,
                                 @RequestParam(defaultValue = "") String search) {

        LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();
        query.eq(Product::getStatus, 1); // 只查上架商品

        if (StringUtils.hasText(search)) {
            query.like(Product::getName, search);
        }

        Page<Product> page = productService.page(
                new Page<>(pageNum, pageSize),
                query
        );

        return Result.success(page);
    }


    @GetMapping("/user/productOne/{id}")
    public Result<?> productOne(@PathVariable Integer id,HttpServletRequest request) {
        Integer type = (Integer) request.getAttribute("type");
        if (type != 2) {
            return Result.error("403", "权限不足无权访问");
        }
        LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();
        query.eq(Product::getId, id);
        Product product = productService.getOne(query);
        if(product == null || product.getStatus()!=1) {
            return Result.error("405","产品为空");
        }
        return Result.success(product);
    }




    /**
     * 添加商品（商家）
     */
    @PostMapping("/add")
    public Result<?> add(@RequestPart("product") Product product,
                         @RequestPart("file") MultipartFile file,
                         HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        Integer type = (Integer) request.getAttribute("type");

        if (userId == null) {
            return Result.error("401", "未登录");
        }
        if (type != 1) {
            return Result.error("403", "非商家无权操作");
        }

        // 创建商家目录
        String uploadDir = PRODUCT_IMG_DIR + username + "/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存图片
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);

        try {
            file.transferTo(dest);

            // 只允许后端设置 merchantId
            product.setMerchantId(userId);
            product.setImageurl("/uploads/product/" + username + "/" + fileName);

            productService.save(product);
            return Result.success();

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("500", "图片上传失败");
        }
    }

    /**
     * 删除商品（商家）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id, HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (userId == null) {
            return Result.error("401", "未登录");
        }

        Product dbProduct = productService.getById(id);
        if (dbProduct == null) {
            return Result.error("404", "商品不存在");
        }

        // 权限校验（只信数据库）
        if (type != 1 || !userId.equals(dbProduct.getMerchantId())) {
            return Result.error("403", "无权删除该商品");
        }

        // 删除图片
        if (StringUtils.hasText(dbProduct.getImageurl())) {
            File file = new File(ROOT_DIR + dbProduct.getImageurl());
            if (file.exists()) {
                file.delete();
            }
        }

        productService.removeById(id);
        return Result.success();
    }

    /**
     * 更新商品（商家）
     */

    @PutMapping("/update")
    public Result<?> update(@RequestPart("product") Product product,
                            @RequestPart(value = "file", required = false) MultipartFile file,
                            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        Integer type = (Integer) request.getAttribute("type");

        if (userId == null) {
            return Result.error("401", "未登录");
        }

        Product dbProduct = productService.getById(product.getId());
        if (dbProduct == null) {
            return Result.error("404", "商品不存在");
        }

        // 权限校验
        if (type != 1 || !userId.equals(dbProduct.getMerchantId())) {
            return Result.error("403", "无权修改该商品");
        }

        String imageUrl = dbProduct.getImageurl();

        // 处理新图片
        if (file != null && !file.isEmpty()) {

            String uploadDir = PRODUCT_IMG_DIR + username + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);

            try {
                file.transferTo(dest);

                // 删除旧图
                if (StringUtils.hasText(imageUrl)) {
                    File oldFile = new File(ROOT_DIR + imageUrl);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                imageUrl = "/uploads/product/" + username + "/" + fileName;

            } catch (IOException e) {
                e.printStackTrace();
                return Result.error("500", "图片上传失败");
            }
        }

        // 白名单更新（防止越权字段）
        Product update = new Product();
        update.setId(product.getId());
        update.setName(product.getName());
        update.setPrice(product.getPrice());
        update.setStock(product.getStock());
        update.setOriginplace(product.getOriginplace());
        update.setCategoryname(product.getCategoryname());
        update.setImageurl(imageUrl);

        productService.updateById(update);
        return Result.success();
    }

    /**
     * 商品上下架（商家）
     */
    @PutMapping("/status")
    public Result<?> changeStatus(@RequestParam Integer id,
                                  @RequestParam Integer status,
                                  HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        Integer type = (Integer) request.getAttribute("type");

        if (userId == null) {
            return Result.error("401", "未登录");
        }

        Product dbProduct = productService.getById(id);
        if (dbProduct == null) {
            return Result.error("404", "商品不存在");
        }

        if (type != 1 || !userId.equals(dbProduct.getMerchantId())) {
            return Result.error("403", "无权操作");
        }

        Product update = new Product();
        update.setId(id);
        update.setStatus(status);

        productService.updateById(update);
        return Result.success();
    }

}
