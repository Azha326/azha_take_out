package com.azha.azha_take_out.service;

import com.azha.azha_take_out.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
