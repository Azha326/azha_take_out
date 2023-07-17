package com.azha.azha_take_out.service;

import com.azha.azha_take_out.dto.DishDto;
import com.azha.azha_take_out.entity.Category;
import com.azha.azha_take_out.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService extends IService<Dish> {
    void remove(List<Long> ids);
    void saveWithFlavor(DishDto dishDto);
    void updateWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavor(Long id);
    DishDto getByCategoryId(Long categoryId);
}
