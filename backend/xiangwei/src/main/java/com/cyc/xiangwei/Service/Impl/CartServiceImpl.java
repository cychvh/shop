package com.cyc.xiangwei.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyc.xiangwei.Service.CartService;
import com.cyc.xiangwei.bean.Cart;
import com.cyc.xiangwei.mapper.CartMapper;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
}
