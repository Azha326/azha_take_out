package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.common.R;
import com.azha.azha_take_out.dto.DishDto;
import com.azha.azha_take_out.dto.SetmealDto;
import com.azha.azha_take_out.entity.Category;
import com.azha.azha_take_out.entity.Dish;
import com.azha.azha_take_out.entity.Setmeal;
import com.azha.azha_take_out.entity.SetmealDish;
import com.azha.azha_take_out.service.CategoryService;
import com.azha.azha_take_out.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> setmealPage = new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage);
        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = setmealList.stream()
                .map(record -> {
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(record, setmealDto);
                    Long categoryId = record.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    String categoryName = category.getName();
                    setmealDto.setCategoryName(categoryName);
                    return setmealDto;
                })
                .collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }
    @DeleteMapping
    @Transactional
    public R<String> deleteById(@RequestParam List<Long> ids){
        log.info("删除分类：{}",ids);
        //保存到数据库
        setmealService.removeWithDish(ids);
        return R.success("删除分类成功");
    }
    //修改时获取指定条目数据
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        //保存到数据库
        SetmealDto setmealDto=setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }
    //修改
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改分类成功");
    }
    @PostMapping("/status/0")
    public R<String> setStatus0(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        setmealList.stream()
                .forEach(dish->dish.setStatus(0));
        setmealService.updateBatchById(setmealList);
        return R.success("批量修改套餐状态成功");
    }

    @PostMapping("/status/1")
    public R<String> setStatus1(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        setmealList.stream()
                .forEach(dish->dish.setStatus(1));
        setmealService.updateBatchById(setmealList);
        return R.success("批量修改套餐状态成功");
    }
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("getCategoryId:{}",setmeal.getCategoryId());
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=setmeal.getCategoryId(),Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(null!=setmeal.getCategoryId(),Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        return R.success(setmealService.list(queryWrapper));
    }
}
