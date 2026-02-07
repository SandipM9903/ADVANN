package com.advann.product_service.service.services;

import com.advann.product_service.dto.ProductRequestDto;
import com.advann.product_service.dto.ProductResponseDto;
import com.advann.product_service.entity.Product;

import java.util.List;

public interface ProductService {

    ProductResponseDto addProduct(ProductRequestDto product);
    List<ProductResponseDto> getAllProducts();
    ProductResponseDto getProductById(Long id);
    ProductResponseDto updateProduct(Long id, ProductRequestDto product);
    void deleteProduct(Long id);
}
