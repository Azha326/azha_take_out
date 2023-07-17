package com.azha.azha_take_out.mapper;

import com.azha.azha_take_out.entity.Category;
import com.azha.azha_take_out.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CategoryMapper extends BaseMapper<Category> {
}
