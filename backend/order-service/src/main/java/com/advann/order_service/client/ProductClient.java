package com.advann.order_service.client;

import com.advann.order_service.dto.ProductResponseDto;
import com.advann.order_service.payload.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<ProductResponseDto> getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/products/reduce-stock/{id}/{quantity}")
    ApiResponse<Object> reduceStock(
            @PathVariable("id") Long productId,
            @PathVariable("quantity") Integer quantity
    );
}