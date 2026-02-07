package com.advann.product_service.controller;

import com.advann.product_service.entity.Product;
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
    public ResponseEntity<ApiResponse<Product>> addProduct(@Valid @RequestBody Product product) {
        Product saveProduct = productService.addProduct(product);
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .success(true)
                .message("Product Created Successfully")
                .data(saveProduct)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        ApiResponse<List<Product>> response = ApiResponse.<List<Product>>builder()
                .success(true)
                .message("All Products Fetched Successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .success(true)
                .message("Product Fetched With id : " + id)
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .success(true)
                .message("Product with id : " + id + ", has been updated successfully")
                .data(product)
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