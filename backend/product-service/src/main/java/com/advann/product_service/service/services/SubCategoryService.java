package com.advann.product_service.service.services;

import com.advann.product_service.dto.SubCategoryRequestDto;
import com.advann.product_service.dto.SubCategoryResponseDto;

import java.util.List;

public interface SubCategoryService {

    SubCategoryResponseDto createSubCategory(SubCategoryRequestDto dto);

    List<SubCategoryResponseDto> getAllSubCategories();

    List<SubCategoryResponseDto> getSubCategoriesByCategory(Long categoryId);

    SubCategoryResponseDto getSubCategoryById(Long id);

    void deleteSubCategory(Long id);
}