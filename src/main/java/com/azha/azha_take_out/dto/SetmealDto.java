package com.azha.azha_take_out.dto;

import com.azha.azha_take_out.entity.DishFlavor;
import com.azha.azha_take_out.entity.Setmeal;
import com.azha.azha_take_out.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes = new ArrayList<>();
    private String categoryName;
    private Integer copies;
}
