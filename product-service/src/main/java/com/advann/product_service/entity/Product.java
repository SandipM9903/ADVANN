package com.advann.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be empty.")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Product price cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than 0.")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "Product quantity cannot be null.")
    @Min(value = 1, message = "Product quantity must be at least 1.")
    @Column(nullable = false)
    private Integer quantity;
}