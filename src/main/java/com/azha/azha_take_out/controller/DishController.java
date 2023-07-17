package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.common.R;
import com.azha.azha_take_out.dto.DishDto;
import com.azha.azha_take_out.entity.Category;
import com.azha.azha_take_out.entity.Dish;
import com.azha.azha_take_out.entity.DishFlavor;
import com.azha.azha_take_out.entity.Employee;
import com.azha.azha_take_out.service.CategoryService;
import com.azha.azha_take_out.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    //分页查询菜品列表
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> dishRecords = pageInfo.getRecords();
        List<DishDto> dishDtoList = dishRecords.stream()
                .map(record -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(record, dishDto);
                    Long categoryId = record.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    String categoryName = category.getName();
                    dishDto.setCategoryName(categoryName);
                    return dishDto;
                })
                .collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }
    /*
    新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @DeleteMapping
    @Transactional
    public R<String> deleteById(@RequestParam List<Long> ids){
        log.info("删除分类：{}",ids);
        //保存到数据库
        dishService.remove(ids);
        return R.success("删除分类成功");
    }
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> dish = dishService.list(queryWrapper);
        return R.success(dish);
    }

    @PostMapping("/status/0")
    public R<String> setStatus0(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> dishList = dishService.list(queryWrapper);
        dishList.stream()
                .forEach(dish->dish.setStatus(0));
        dishService.updateBatchById(dishList);
        return R.success("批量修改菜品状态成功");
    }

    @PostMapping("/status/1")
    public R<String> setStatus1(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> dishList = dishService.list(queryWrapper);
        dishList.stream()
                .forEach(dish->dish.setStatus(1));
        dishService.updateBatchById(dishList);
        return R.success("批量修改菜品状态成功");
    }
}
