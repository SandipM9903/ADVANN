package com.advann.product_service.controller;

import com.advann.product_service.payload.ApiResponse;
import com.advann.product_service.service.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductService productService;

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<ApiResponse<Void>> reserveStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        productService.reserveStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock reserved successfully", null));
    }

    @PostMapping("/{productId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        productService.confirmStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock confirmed successfully", null));
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<ApiResponse<Void>> releaseStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        productService.releaseStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock released successfully", null));
    }
}