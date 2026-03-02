package com.advann.product_service.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
}