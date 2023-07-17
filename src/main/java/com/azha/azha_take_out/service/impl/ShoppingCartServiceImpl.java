package com.azha.azha_take_out.service.impl;

import com.azha.azha_take_out.entity.ShoppingCart;
import com.azha.azha_take_out.service.ShoppingCartMapper;
import com.azha.azha_take_out.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
