package com.azha.azha_take_out.service;

import com.azha.azha_take_out.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService extends IService<Category> {
    void remove(Long id);

}
