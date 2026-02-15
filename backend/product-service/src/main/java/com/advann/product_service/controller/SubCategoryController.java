package com.advann.product_service.controller;

import com.advann.product_service.dto.SubCategoryRequestDto;
import com.advann.product_service.dto.SubCategoryResponseDto;
import com.advann.product_service.payload.ApiResponse;
import com.advann.product_service.service.services.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @Operation(summary = "Create SubCategory", description = "Creates a new subcategory under a category")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SubCategory created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<SubCategoryResponseDto>> createSubCategory(
            @Valid @RequestBody SubCategoryRequestDto dto
    ) {

        SubCategoryResponseDto savedSubCategory = subCategoryService.createSubCategory(dto);

        ApiResponse<SubCategoryResponseDto> response = ApiResponse.<SubCategoryResponseDto>builder()
                .success(true)
                .message("SubCategory created successfully")
                .data(savedSubCategory)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get All SubCategories", description = "Fetch all subcategories from database")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SubCategories fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubCategoryResponseDto>>> getAllSubCategories() {

        List<SubCategoryResponseDto> subCategories = subCategoryService.getAllSubCategories();

        ApiResponse<List<SubCategoryResponseDto>> response = ApiResponse.<List<SubCategoryResponseDto>>builder()
                .success(true)
                .message("SubCategories fetched successfully")
                .data(subCategories)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get SubCategories By Category", description = "Fetch subcategories using category id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SubCategories fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<SubCategoryResponseDto>>> getSubCategoriesByCategory(
            @PathVariable Long categoryId
    ) {

        List<SubCategoryResponseDto> subCategories =
                subCategoryService.getSubCategoriesByCategory(categoryId);

        ApiResponse<List<SubCategoryResponseDto>> response = ApiResponse.<List<SubCategoryResponseDto>>builder()
                .success(true)
                .message("SubCategories fetched successfully")
                .data(subCategories)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get SubCategory By Id", description = "Fetch subcategory using subcategory id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SubCategory fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SubCategory not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCategoryResponseDto>> getSubCategoryById(
            @PathVariable Long id
    ) {

        SubCategoryResponseDto subCategory = subCategoryService.getSubCategoryById(id);

        ApiResponse<SubCategoryResponseDto> response = ApiResponse.<SubCategoryResponseDto>builder()
                .success(true)
                .message("SubCategory fetched successfully")
                .data(subCategory)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete SubCategory", description = "Delete subcategory using subcategory id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SubCategory deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SubCategory not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSubCategory(
            @PathVariable Long id
    ) {

        subCategoryService.deleteSubCategory(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("SubCategory deleted successfully")
                .data("SubCategory deleted with id : " + id)
                .build();

        return ResponseEntity.ok(response);
    }
}