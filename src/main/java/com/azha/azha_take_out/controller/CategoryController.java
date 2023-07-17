package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.common.CustomException;
import com.azha.azha_take_out.common.R;
import com.azha.azha_take_out.entity.Category;
import com.azha.azha_take_out.entity.Dish;
import com.azha.azha_take_out.entity.Employee;
import com.azha.azha_take_out.entity.Setmeal;
import com.azha.azha_take_out.service.CategoryService;
import com.azha.azha_take_out.service.DishService;
import com.azha.azha_take_out.service.EmployeeService;
import com.azha.azha_take_out.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    //分页查询菜品列表
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Category::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Category::getUpdateTime);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByDesc(Category::getUpdateTime);
        //执行查询
        List<Category> list = categoryService.list(queryWrapper);
        log.info(queryWrapper.getSqlSelect());
        return R.success(list);
    }

    @PostMapping
    public R<String> save(@RequestBody Category category, HttpServletRequest request){
        log.info("新增分类：{}",category.toString());
        //设置无需输入的初始信息
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Long empId = (Long)request.getSession().getAttribute("employee");
        category.setCreateUser(empId);
        category.setUpdateUser(empId);
        //保存到数据库
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    //删除分类
    @DeleteMapping
    public R<String> deleteById(Long ids){
        log.info("删除分类：{}",ids);
        //保存到数据库
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }
}
