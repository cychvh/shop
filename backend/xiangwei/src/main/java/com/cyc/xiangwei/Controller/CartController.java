package com.cyc.xiangwei.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyc.xiangwei.Service.CartService;
import com.cyc.xiangwei.Service.ProductService;
import com.cyc.xiangwei.bean.Cart;
import com.cyc.xiangwei.bean.CartVo;
import com.cyc.xiangwei.bean.Product;
import com.cyc.xiangwei.bean.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//购物车控制层
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "5") Integer pageSize,
                          HttpServletRequest request) {
        //不要相信前端传过来的数据
        Integer userId = (Integer) request.getAttribute("userId");
        LambdaQueryWrapper<Cart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        cartLambdaQueryWrapper.eq(Cart::getUserid, userId);

        Page<Cart> page = cartService.page(new Page<>(pageNum, pageSize), cartLambdaQueryWrapper);
        Page<CartVo> cartVoPage = new Page<>();
        BeanUtils.copyProperties(page, cartVoPage);
        List<CartVo> cartVos = new ArrayList<>();
        for (Cart cart : page.getRecords()) {
            CartVo cartVo = new CartVo();
            BeanUtils.copyProperties(cart, cartVo);
            Product product = productService.getById(cart.getProductid());
            if (product != null) {
                cartVo.setProductName(product.getName());
                cartVo.setPrice(product.getPrice());
                cartVo.setImageurl(product.getImageurl());
            }
            cartVos.add(cartVo);
        }
        cartVoPage.setRecords(cartVos);
        return Result.success(cartVoPage);
    }

    @PostMapping("/addcart")
    public Result<?> addCart(@RequestBody Cart cart, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        //强制只能填写自己的id且这个id是JWT中获取username然后在调用数据库查的
        cart.setUserid(userId);
        if (cart.getQuantity() <= 0) {
            return Result.error("400", "数量不合法");
        }
        LambdaQueryWrapper<Cart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(Cart::getUserid, userId)
                .eq(Cart::getProductid, cart.getProductid());
        Cart exist = cartService.getOne(cartLambdaQueryWrapper, false);

        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + cart.getQuantity());
            cartService.updateById(exist);
        } else {
            cartService.save(cart);
        }
        return Result.success();
    }

    @PutMapping("/updateCart")
    public Result<?> updateCart(@RequestBody Cart cart, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Cart dbCart = cartService.getById(cart.getId());
        if (dbCart == null || !dbCart.getUserid().equals(userId)) {
            return Result.error("405", "权限不够");
        }
        if (cart.getQuantity() <= 0) {
            return Result.error("400", "数量不合法");
        }
        dbCart.setQuantity(cart.getQuantity());
        if (cartService.updateById(dbCart)) {
            return Result.success();
        }
        return Result.error("500", "修改失败");

    }

    @DeleteMapping("/delCart")
    public Result<?> delCart(@RequestParam Integer cartId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Cart byId = cartService.getById(cartId);
        if (byId == null || !byId.getUserid().equals(userId)) {
            return Result.error("405", "权限不够");
        }
        if (cartService.removeById(cartId)) {
            return Result.success();
        }
        return Result.error("500", "删除失败");
    }
}
