package com.azha.azha_take_out.service.impl;

import com.azha.azha_take_out.common.BaseContext;
import com.azha.azha_take_out.common.CustomException;
import com.azha.azha_take_out.entity.*;
import com.azha.azha_take_out.mapper.OrdersMapper;
import com.azha.azha_take_out.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    /*
    用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //当前用户是谁
        Long userId= BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList=shoppingCartService.list(wrapper);
        if(shoppingCartList==null || shoppingCartList.size()==0){
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据以及地址信息
        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new CustomException("地址信息有误，不能下单");
        }
        //向订单表插入数据
        Long orderId = IdWorker.getId();
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(2);
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetailList = shoppingCartList.stream()
                .map(item->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setAmount(item.getAmount());
                    orderDetail.setDishFlavor(item.getDishFlavor());
                    orderDetail.setDishId(item.getDishId());
                    orderDetail.setId(item.getId());
                    orderDetail.setImage(item.getImage());
                    orderDetail.setName(item.getName());
                    orderDetail.setNumber(item.getNumber());
                    orderDetail.setOrderId(item.getUserId());
                    orderDetail.setSetmealId(item.getSetmealId());
                    amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
                    return orderDetail;
                })
                .collect(Collectors.toList());
        orders.setAmount(new BigDecimal(amount.get()));//计算总金额
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                        + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                        + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        //向明细表插入数据
        orderDetailService.saveBatch(orderDetailList);
        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }
}
