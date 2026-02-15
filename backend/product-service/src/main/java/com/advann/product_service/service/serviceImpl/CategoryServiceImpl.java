package com.advann.product_service.service.serviceImpl;

import com.advann.product_service.dto.CategoryRequestDto;
import com.advann.product_service.dto.CategoryResponseDto;
import com.advann.product_service.entity.Category;
import com.advann.product_service.exceptions.ResourceNotFoundException;
import com.advann.product_service.repository.CategoryRepository;
import com.advann.product_service.service.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {

        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new RuntimeException("Category already exists: " + requestDto.getName());
        }

        Category category = Category.builder()
                .name(requestDto.getName())
                .build();

        Category saved = categoryRepository.save(category);

        return modelMapper.map(saved, CategoryResponseDto.class);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(cat -> modelMapper.map(cat, CategoryResponseDto.class))
                .toList();
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return modelMapper.map(category, CategoryResponseDto.class);
    }

    @Override
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryRepository.delete(category);
    }
}