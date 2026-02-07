package com.advann.product_service.controller;

import com.advann.product_service.dto.ProductRequestDto;
import com.advann.product_service.dto.ProductResponseDto;
import com.advann.product_service.payload.ApiResponse;
import com.advann.product_service.service.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> addProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {

        ProductResponseDto savedProduct = productService.addProduct(productRequestDto);

        ApiResponse<ProductResponseDto> response = ApiResponse.<ProductResponseDto>builder()
                .success(true)
                .message("Product created successfully")
                .data(savedProduct)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllProducts() {

        List<ProductResponseDto> products = productService.getAllProducts();

        ApiResponse<List<ProductResponseDto>> response = ApiResponse.<List<ProductResponseDto>>builder()
                .success(true)
                .message("Products fetched successfully")
                .data(products)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {

        ProductResponseDto product = productService.getProductById(id);

        ApiResponse<ProductResponseDto> response = ApiResponse.<ProductResponseDto>builder()
                .success(true)
                .message("Product fetched successfully")
                .data(product)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(@PathVariable Long id,
                                                                         @Valid @RequestBody ProductRequestDto productRequestDto) {

        ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);

        ApiResponse<ProductResponseDto> response = ApiResponse.<ProductResponseDto>builder()
                .success(true)
                .message("Product updated successfully")
                .data(updatedProduct)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Product deleted successfully")
                .data("Product deleted with id : " + id)
                .build();

        return ResponseEntity.ok(response);
    }
}