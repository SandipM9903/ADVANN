package com.advann.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be empty.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Product price cannot be empty.")
    @Column(nullable = false)
    private BigDecimal price;

    @NotBlank(message = "Product quantity cannot be empty.")
    @Column(nullable = false)
    private Integer quantity;
}
