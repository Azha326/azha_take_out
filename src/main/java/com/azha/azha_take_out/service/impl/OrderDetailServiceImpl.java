package com.azha.azha_take_out.service.impl;

import com.azha.azha_take_out.entity.OrderDetail;
import com.azha.azha_take_out.mapper.OrderDetailMapper;
import com.azha.azha_take_out.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
