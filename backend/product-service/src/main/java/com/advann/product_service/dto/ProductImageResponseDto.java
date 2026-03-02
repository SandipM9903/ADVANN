package com.advann.product_service.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String imagePath;
    private String imageUrl;

    private String thumbnailPath;
    private String thumbnailUrl;
}