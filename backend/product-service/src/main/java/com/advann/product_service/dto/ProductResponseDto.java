package com.advann.product_service.dto;

import lombok.*;
import java.io.Serializable; // 1. Add this import
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imagePath;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
}