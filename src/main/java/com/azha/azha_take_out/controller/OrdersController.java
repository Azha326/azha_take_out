package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.common.BaseContext;
import com.azha.azha_take_out.common.R;
import com.azha.azha_take_out.entity.Category;
import com.azha.azha_take_out.entity.Orders;
import com.azha.azha_take_out.service.OrdersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    OrdersService ordersService;
    /*
    用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("订单提交成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
}
