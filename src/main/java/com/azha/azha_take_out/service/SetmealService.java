package com.azha.azha_take_out.service;

import com.azha.azha_take_out.dto.DishDto;
import com.azha.azha_take_out.dto.SetmealDto;
import com.azha.azha_take_out.entity.Setmeal;
import com.azha.azha_take_out.entity.SetmealDish;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);
    void removeWithDish(List<Long> ids);
    SetmealDto getByIdWithDish(Long id);
    void updateWithDish(SetmealDto setmealDto);
}
