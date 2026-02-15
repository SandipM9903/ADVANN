package com.advann.product_service.service.services;

import com.advann.product_service.dto.CategoryRequestDto;
import com.advann.product_service.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto requestDto);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(Long id);

    void deleteCategory(Long id);
}