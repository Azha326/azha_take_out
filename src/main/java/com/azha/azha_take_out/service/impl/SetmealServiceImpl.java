package com.azha.azha_take_out.service.impl;

import com.azha.azha_take_out.common.CustomException;
import com.azha.azha_take_out.dto.SetmealDto;
import com.azha.azha_take_out.entity.Dish;
import com.azha.azha_take_out.entity.Setmeal;
import com.azha.azha_take_out.entity.SetmealDish;
import com.azha.azha_take_out.mapper.SetmealMapper;
import com.azha.azha_take_out.service.SetmealDishService;
import com.azha.azha_take_out.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        log.info("saveWithDish启动");
        Long setmealId=setmealDto.getId();
        List<SetmealDish> dishes = setmealDto.getSetmealDishes().stream()
                .peek(dish -> dish.setSetmealId(setmealId))
                .collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询套餐状态，确认是否可以删除
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);//count为以上查询语句结果计数
        if(count>0){
            //符合上述状态的不允许删除，直接抛出异常
            throw new CustomException("套餐还在售卖中，不得删除");
        }
        this.removeByIds(ids);
        //删除关联数据表
        LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper2);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal=this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //先清理之前的dish参数
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //添加新的dish参数
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream()
                .peek(dish->dish.setSetmealId(setmealDto.getId()))
                .collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
