package com.advann.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    private String imagePath;
    private String imageUrl;

    private Long categoryId;
    private String categoryName;

    private Long subCategoryId;
    private String subCategoryName;
}
