package com.shui.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.entity.Category;
import com.shui.mapper.CategoryMapper;
import com.shui.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
