package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.common.BaseContext;
import com.azha.azha_take_out.common.R;
import com.azha.azha_take_out.entity.AddressBook;
import com.azha.azha_take_out.entity.ShoppingCart;
import com.azha.azha_take_out.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController  {
    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);
        //设置用户ID，指定是哪个用户的购物车数据
        Long currentId=BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或者套餐是否已经在购物车当中
        Long dishId=shoppingCart.getDishId();
        Long setMealId=shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        if(setMealId!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,setMealId);
        }
        ShoppingCart shoppingCart_getOne=shoppingCartService.getOne(queryWrapper);
        //如果已经存在就在原来的基础上加1
        if(shoppingCart_getOne!=null){
            Integer number = shoppingCart_getOne.getNumber();
            shoppingCart_getOne.setNumber(number+1);
            shoppingCartService.updateById(shoppingCart_getOne);
        }else {
            //如果不存在，则添加到购物车，数量默认1
            shoppingCart_getOne = shoppingCart;
            shoppingCart_getOne.setNumber(1);
            shoppingCartService.save(shoppingCart_getOne);
        }
        return R.success(shoppingCart_getOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);
        //设置用户ID，指定是哪个用户的购物车数据
        Long currentId=BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或者套餐是否已经在购物车当中
        Long dishId=shoppingCart.getDishId();
        Long setMealId=shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        if(setMealId!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,setMealId);
        }
        ShoppingCart shoppingCart_getOne=shoppingCartService.getOne(queryWrapper);
        //在原来的基础上－1
        if(shoppingCart_getOne!=null){
            Integer number = shoppingCart_getOne.getNumber();
            shoppingCart_getOne.setNumber(number-1);
            if(number-1>0)shoppingCartService.updateById(shoppingCart_getOne);
            else shoppingCartService.remove(queryWrapper);
        }
        return R.success(shoppingCart_getOne);
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        //根据userid把购物车清了就行了
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=shoppingCart.getUserId(),ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        return R.success(shoppingCartService.list(queryWrapper));
    }
}
