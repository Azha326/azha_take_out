package com.azha.azha_take_out.service.impl;

import com.azha.azha_take_out.common.CustomException;
import com.azha.azha_take_out.dto.DishDto;
import com.azha.azha_take_out.entity.Dish;
import com.azha.azha_take_out.entity.DishFlavor;
import com.azha.azha_take_out.entity.Setmeal;
import com.azha.azha_take_out.mapper.DishMapper;
import com.azha.azha_take_out.service.DishFlavorService;
import com.azha.azha_take_out.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    public void remove(List<Long> ids){
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
//        int count1 = dishService.count(dishLambdaQueryWrapper);
//        if(count1>0){
//            //说明该分类关联了菜品，抛出异常
//            throw new CustomException("当前分类下关联了菜品，不能删除");
//        }
//        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
//        int count2 = setmealService.count(setmealLambdaQueryWrapper);
//        if(count2>0){
//            //说明该分类关联了套餐，抛出异常
//            throw new CustomException("当前分类下关联了套餐，不能删除");
//        }
        super.removeByIds(ids);
    }

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息
        this.save(dishDto);
        Long dishId=dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors().stream()
                .peek(flavor -> flavor.setDishId(dishId))
                .collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //先清理当前菜品已经对应口味信息delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过的口味数据insert
        List<DishFlavor> flavors = dishDto.getFlavors().stream()
                .peek(flavor -> flavor.setDishId(dishDto.getId()))
                .collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id){
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //复制属性
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应口味信息，从dish_flavor查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public DishDto getByCategoryId(Long categoryId) {
        return null;
    }
}

