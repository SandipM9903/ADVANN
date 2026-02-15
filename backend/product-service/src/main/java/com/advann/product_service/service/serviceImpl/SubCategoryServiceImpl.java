package com.advann.product_service.service.serviceImpl;

import com.advann.product_service.dto.SubCategoryRequestDto;
import com.advann.product_service.dto.SubCategoryResponseDto;
import com.advann.product_service.entity.Category;
import com.advann.product_service.entity.SubCategory;
import com.advann.product_service.exceptions.ResourceAlreadyExistsException;
import com.advann.product_service.exceptions.ResourceNotFoundException;
import com.advann.product_service.repository.CategoryRepository;
import com.advann.product_service.repository.SubCategoryRepository;
import com.advann.product_service.service.services.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public SubCategoryResponseDto createSubCategory(SubCategoryRequestDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category with id : " + dto.getCategoryId()));

        if (subCategoryRepository.existsByNameIgnoreCaseAndCategoryId(dto.getName(), dto.getCategoryId())) {
            throw new ResourceAlreadyExistsException("SubCategory already exists in this category");
        }

        SubCategory subCategory = SubCategory.builder()
                .name(dto.getName())
                .category(category)
                .build();

        SubCategory saved = subCategoryRepository.save(subCategory);

        return SubCategoryResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    @Override
    public List<SubCategoryResponseDto> getAllSubCategories() {

        return subCategoryRepository.findAll()
                .stream()
                .map(sc -> SubCategoryResponseDto.builder()
                        .id(sc.getId())
                        .name(sc.getName())
                        .categoryId(sc.getCategory().getId())
                        .categoryName(sc.getCategory().getName())
                        .build())
                .toList();
    }

    @Override
    public List<SubCategoryResponseDto> getSubCategoriesByCategory(Long categoryId) {

        // ✅ Check if category exists
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category is not present with id : " + categoryId));

        return subCategoryRepository.findByCategoryId(categoryId)
                .stream()
                .map(sc -> SubCategoryResponseDto.builder()
                        .id(sc.getId())
                        .name(sc.getName())
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build())
                .toList();
    }

    @Override
    public SubCategoryResponseDto getSubCategoryById(Long id) {

        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory with id : " + id));

        return SubCategoryResponseDto.builder()
                .id(subCategory.getId())
                .name(subCategory.getName())
                .categoryId(subCategory.getCategory().getId())
                .categoryName(subCategory.getCategory().getName())
                .build();
    }

    @Override
    public void deleteSubCategory(Long id) {

        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id : " + id));

        subCategoryRepository.delete(subCategory);
    }
}