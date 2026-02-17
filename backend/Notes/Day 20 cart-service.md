✅ Step 1: Create New Microservice



Create a new Spring Boot project:



📌 Service Name: cart-service

📌 Port: 8082 (keep product-service on 8081)



✅ Step 2: Add Dependencies (pom.xml)



You need these dependencies:



Spring Web



Spring Data JPA



PostgreSQL Driver



Lombok



Validation



OpenFeign (to call product-service)



***OpenFeign is used in Spring Boot microservices to make REST API calls to other services easily, without writing a lot of boilerplate code like RestTemplate or manual HttpClient.***



✅ Step 3: DB Config (application.yml)



Example:



server:

&nbsp; port: 8082



spring:

&nbsp; application:

&nbsp;   name: cart-service



&nbsp; datasource:

&nbsp;   url: jdbc:postgresql://localhost:5432/advann\_cart\_db

&nbsp;   username: postgres

&nbsp;   password: root

&nbsp;   driver-class-name: org.postgresql.Driver



&nbsp; jpa:

&nbsp;   hibernate:

&nbsp;     ddl-auto: update

&nbsp;   show-sql: true

&nbsp;   open-in-view: false



eureka:

&nbsp; client:

&nbsp;   service-url:

&nbsp;     defaultZone: http://localhost:8761/eureka/



management:

&nbsp; endpoints:

&nbsp;   web:

&nbsp;     exposure:

&nbsp;       include: health,info



&nbsp; endpoint:

&nbsp;   health:

&nbsp;     show-details: always



info:

&nbsp; app:

&nbsp;   name: Cart Service

&nbsp;   description: Cart Microservice for Advann

&nbsp;   version: 1.0.0



logging:

&nbsp; level:

&nbsp;   com.advann.cart\_service: INFO



springdoc:

&nbsp; api-docs:

&nbsp;   path: /v3/api-docs



&nbsp; swagger-ui:

&nbsp;   path: /swagger-ui.html

&nbsp;   config-url: /cart-service/v3/api-docs/swagger-config

&nbsp;   url: /cart-service/v3/api-docs



==============================================================================================



pom.xml



<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"

&nbsp;		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

&nbsp;		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">



&nbsp;	<modelVersion>4.0.0</modelVersion>



&nbsp;	<parent>

&nbsp;		<groupId>org.springframework.boot</groupId>

&nbsp;		<artifactId>spring-boot-starter-parent</artifactId>

&nbsp;		<version>3.3.8</version>

&nbsp;		<relativePath/>

&nbsp;	</parent>



&nbsp;	<groupId>com.advann</groupId>

&nbsp;	<artifactId>cart-service</artifactId>

&nbsp;	<version>0.0.1-SNAPSHOT</version>

&nbsp;	<name>cart-service</name>

&nbsp;	<description>Cart Service for Advann Microservices</description>



&nbsp;	<properties>

&nbsp;		<java.version>17</java.version>

&nbsp;		<spring-cloud.version>2023.0.5</spring-cloud.version>

&nbsp;	</properties>



&nbsp;	<dependencies>



&nbsp;		<!-- Spring Boot Web -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.boot</groupId>

&nbsp;			<artifactId>spring-boot-starter-web</artifactId>

&nbsp;		</dependency>



&nbsp;		<!-- Spring Data JPA -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.boot</groupId>

&nbsp;			<artifactId>spring-boot-starter-data-jpa</artifactId>

&nbsp;		</dependency>



&nbsp;		<!-- Validation -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.boot</groupId>

&nbsp;			<artifactId>spring-boot-starter-validation</artifactId>

&nbsp;		</dependency>



&nbsp;		<!-- Eureka Client -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.cloud</groupId>

&nbsp;			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>

&nbsp;		</dependency>



&nbsp;		<!-- Feign Client -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.cloud</groupId>

&nbsp;			<artifactId>spring-cloud-starter-openfeign</artifactId>

&nbsp;		</dependency>



&nbsp;		<!-- PostgreSQL -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.postgresql</groupId>

&nbsp;			<artifactId>postgresql</artifactId>

&nbsp;			<scope>runtime</scope>

&nbsp;		</dependency>



&nbsp;		<!-- Lombok -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.projectlombok</groupId>

&nbsp;			<artifactId>lombok</artifactId>

&nbsp;			<version>1.18.34</version>

&nbsp;			<optional>true</optional>

&nbsp;		</dependency>



&nbsp;		<!-- Swagger OpenAPI -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springdoc</groupId>

&nbsp;			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>

&nbsp;			<version>2.5.0</version>

&nbsp;		</dependency>



&nbsp;		<!-- Actuator -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.boot</groupId>

&nbsp;			<artifactId>spring-boot-starter-actuator</artifactId>

&nbsp;		</dependency>



&nbsp;		<!-- ModelMapper (Optional but recommended) -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.modelmapper</groupId>

&nbsp;			<artifactId>modelmapper</artifactId>

&nbsp;			<version>3.2.0</version>

&nbsp;		</dependency>



&nbsp;		<!-- Spring Boot Test -->

&nbsp;		<dependency>

&nbsp;			<groupId>org.springframework.boot</groupId>

&nbsp;			<artifactId>spring-boot-starter-test</artifactId>

&nbsp;			<scope>test</scope>

&nbsp;		</dependency>



&nbsp;	</dependencies>



&nbsp;	<!-- Spring Cloud Dependency Management -->

&nbsp;	<dependencyManagement>

&nbsp;		<dependencies>

&nbsp;			<dependency>

&nbsp;				<groupId>org.springframework.cloud</groupId>

&nbsp;				<artifactId>spring-cloud-dependencies</artifactId>

&nbsp;				<version>${spring-cloud.version}</version>

&nbsp;				<type>pom</type>

&nbsp;				<scope>import</scope>

&nbsp;			</dependency>

&nbsp;		</dependencies>

&nbsp;	</dependencyManagement>



&nbsp;	<build>

&nbsp;		<plugins>



&nbsp;			<!-- Maven Compiler Plugin -->

&nbsp;			<plugin>

&nbsp;				<groupId>org.apache.maven.plugins</groupId>

&nbsp;				<artifactId>maven-compiler-plugin</artifactId>

&nbsp;				<configuration>

&nbsp;					<annotationProcessorPaths>

&nbsp;						<path>

&nbsp;							<groupId>org.projectlombok</groupId>

&nbsp;							<artifactId>lombok</artifactId>

&nbsp;							<version>1.18.34</version>

&nbsp;						</path>

&nbsp;					</annotationProcessorPaths>

&nbsp;				</configuration>

&nbsp;			</plugin>



&nbsp;			<!-- Spring Boot Plugin -->

&nbsp;			<plugin>

&nbsp;				<groupId>org.springframework.boot</groupId>

&nbsp;				<artifactId>spring-boot-maven-plugin</artifactId>

&nbsp;				<configuration>

&nbsp;					<excludes>

&nbsp;						<exclude>

&nbsp;							<groupId>org.projectlombok</groupId>

&nbsp;							<artifactId>lombok</artifactId>

&nbsp;						</exclude>

&nbsp;					</excludes>

&nbsp;				</configuration>

&nbsp;			</plugin>



&nbsp;		</plugins>

&nbsp;	</build>



</project>



=====================================================================================================



Perfect Sandip ✅ let’s start with Cart Service Entities.



We will create 2 tables:



cart



cart\_items



Because one cart can have multiple items.



✅ 1. Cart Entity



📌 File: src/main/java/com/advann/cart\_service/entity/Cart.java



package com.advann.cart\_service.entity;



import jakarta.persistence.\*;

import lombok.\*;



import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;



@Entity

@Table(name = "cart")

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Cart {



&nbsp;   @Id

&nbsp;   @GeneratedValue(strategy = GenerationType.IDENTITY)

&nbsp;   private Long id;



&nbsp;   // for now we store userId directly (later we can connect with user-service)

&nbsp;   @Column(nullable = false)

&nbsp;   private Long userId;



&nbsp;   @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)

&nbsp;   private List<CartItem> cartItems = new ArrayList<>();



&nbsp;   @Column(nullable = false)

&nbsp;   private LocalDateTime createdAt;



&nbsp;   @Column(nullable = false)

&nbsp;   private LocalDateTime updatedAt;



&nbsp;   @PrePersist

&nbsp;   public void onCreate() {

&nbsp;       this.createdAt = LocalDateTime.now();

&nbsp;       this.updatedAt = LocalDateTime.now();

&nbsp;   }



&nbsp;   @PreUpdate

&nbsp;   public void onUpdate() {

&nbsp;       this.updatedAt = LocalDateTime.now();

&nbsp;   }

}

✅ 2. CartItem Entity



📌 File: src/main/java/com/advann/cart\_service/entity/CartItem.java



package com.advann.cart\_service.entity;



import jakarta.persistence.\*;

import lombok.\*;



import java.math.BigDecimal;

import java.time.LocalDateTime;



@Entity

@Table(name = "cart\_items")

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class CartItem {



&nbsp;   @Id

&nbsp;   @GeneratedValue(strategy = GenerationType.IDENTITY)

&nbsp;   private Long id;



&nbsp;   // Many cart items belong to one cart

&nbsp;   @ManyToOne(fetch = FetchType.LAZY)

&nbsp;   @JoinColumn(name = "cart\_id", nullable = false)

&nbsp;   private Cart cart;



&nbsp;   @Column(nullable = false)

&nbsp;   private Long productId;



&nbsp;   @Column(nullable = false)

&nbsp;   private Integer quantity;



&nbsp;   // store price at that time (important for future discounts changes)

&nbsp;   @Column(nullable = false)

&nbsp;   private BigDecimal price;



&nbsp;   @Column(nullable = false)

&nbsp;   private BigDecimal totalPrice;



&nbsp;   @Column(nullable = false)

&nbsp;   private LocalDateTime createdAt;



&nbsp;   @Column(nullable = false)

&nbsp;   private LocalDateTime updatedAt;



&nbsp;   @PrePersist

&nbsp;   public void onCreate() {

&nbsp;       this.createdAt = LocalDateTime.now();

&nbsp;       this.updatedAt = LocalDateTime.now();



&nbsp;       if (this.price != null \&\& this.quantity != null) {

&nbsp;           this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity));

&nbsp;       }

&nbsp;   }



&nbsp;   @PreUpdate

&nbsp;   public void onUpdate() {

&nbsp;       this.updatedAt = LocalDateTime.now();



&nbsp;       if (this.price != null \&\& this.quantity != null) {

&nbsp;           this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity));

&nbsp;       }

&nbsp;   }

}

✅ Why we added price and totalPrice in CartItem?



Because product price may change later in product-service, but cart should preserve the price at the time user added it.



============================================================================================================



✅ 1. CartRepository



📌 File: src/main/java/com/advann/cart\_service/repository/CartRepository.java



package com.advann.cart\_service.repository;



import com.advann.cart\_service.entity.Cart;

import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;



public interface CartRepository extends JpaRepository<Cart, Long> {



&nbsp;   Optional<Cart> findByUserId(Long userId);



&nbsp;   boolean existsByUserId(Long userId);

}

✅ 2. CartItemRepository



📌 File: src/main/java/com/advann/cart\_service/repository/CartItemRepository.java



package com.advann.cart\_service.repository;



import com.advann.cart\_service.entity.CartItem;

import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

import java.util.Optional;



public interface CartItemRepository extends JpaRepository<CartItem, Long> {



&nbsp;   List<CartItem> findByCartId(Long cartId);



&nbsp;   Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);



&nbsp;   boolean existsByCartIdAndProductId(Long cartId, Long productId);



&nbsp;   void deleteByCartId(Long cartId);

}

✅ What we can do with these methods?

CartRepository



find cart by userId



check if cart exists



CartItemRepository



find all items in cart



check if product already exists in cart



delete all items when cart cleared



=========================================================================================================



✅ 1. CartRequestDto



📌 File: src/main/java/com/advann/cart\_service/dto/CartRequestDto.java



package com.advann.cart\_service.dto;



import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotNull;

import lombok.\*;



@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class CartRequestDto {



&nbsp;   @NotNull(message = "UserId is required")

&nbsp;   private Long userId;



&nbsp;   @NotNull(message = "ProductId is required")

&nbsp;   private Long productId;



&nbsp;   @NotNull(message = "Quantity is required")

&nbsp;   @Min(value = 1, message = "Quantity must be at least 1")

&nbsp;   private Integer quantity;

}

✅ 2. CartItemResponseDto



📌 File: src/main/java/com/advann/cart\_service/dto/CartItemResponseDto.java



package com.advann.cart\_service.dto;



import lombok.\*;



import java.math.BigDecimal;



@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class CartItemResponseDto {



&nbsp;   private Long id;



&nbsp;   private Long productId;



&nbsp;   private String productName;



&nbsp;   private String productImage;



&nbsp;   private BigDecimal price;



&nbsp;   private Integer quantity;



&nbsp;   private BigDecimal totalPrice;

}

✅ 3. CartResponseDto



📌 File: src/main/java/com/advann/cart\_service/dto/CartResponseDto.java



package com.advann.cart\_service.dto;



import lombok.\*;



import java.math.BigDecimal;

import java.util.List;



@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class CartResponseDto {



&nbsp;   private Long cartId;



&nbsp;   private Long userId;



&nbsp;   private List<CartItemResponseDto> items;



&nbsp;   private BigDecimal grandTotal;



&nbsp;   private Integer totalItems;

}

🔥 Why we added productName + productImage in response?



Because cart-service will call product-service using Feign Client to fetch product details and show them in cart response.



================================================================================================================



Now we will create feign Client to call product api



✅ 1. Create Product DTO (Cart Service Side)



Because Feign needs a response structure.



📌 File: src/main/java/com/advann/cart\_service/dto/ProductResponseDto.java



package com.advann.cart\_service.dto;



import lombok.\*;



import java.math.BigDecimal;



@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class ProductResponseDto {



&nbsp;   private Long id;

&nbsp;   private String name;

&nbsp;   private BigDecimal price;

&nbsp;   private Integer quantity;

&nbsp;   private String imagePath;

&nbsp;   private String imageUrl;

&nbsp;   private Long categoryId;

&nbsp;   private String categoryName;

&nbsp;   private Long subCategoryId;

&nbsp;   private String subCategoryName;

}

✅ 2. ApiResponse Wrapper DTO (Same as Product Service)



Because your product-service returns response like:



{

&nbsp; "success": true,

&nbsp; "message": "Product fetched successfully",

&nbsp; "data": {...}

}



So cart-service must match it.



📌 File: src/main/java/com/advann/cart\_service/payload/ApiResponse.java



package com.advann.cart\_service.payload;



import lombok.\*;



@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class ApiResponse<T> {



&nbsp;   private boolean success;

&nbsp;   private String message;

&nbsp;   private T data;

}

✅ 3. Feign Client (ProductClient)



📌 File: src/main/java/com/advann/cart\_service/client/ProductClient.java



package com.advann.cart\_service.client;



import com.advann.cart\_service.dto.ProductResponseDto;

import com.advann.cart\_service.payload.ApiResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "product-service")

public interface ProductClient {



&nbsp;   @GetMapping("/api/products/{id}")

&nbsp;   ApiResponse<ProductResponseDto> getProductById(@PathVariable("id") Long id);

}



✅ Here name="product-service" must match your product-service application name from yml.



✅ 4. Enable Feign Client in CartService main class



📌 File: CartServiceApplication.java



package com.advann.cart\_service;



import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;



@SpringBootApplication

@EnableFeignClients

public class CartServiceApplication {



&nbsp;   public static void main(String\[] args) {

&nbsp;       SpringApplication.run(CartServiceApplication.class, args);

&nbsp;   }

}

✅ 5. CartService Interface



📌 File: src/main/java/com/advann/cart\_service/service/services/CartService.java



package com.advann.cart\_service.service.services;



import com.advann.cart\_service.dto.CartRequestDto;

import com.advann.cart\_service.dto.CartResponseDto;



public interface CartService {



&nbsp;   CartResponseDto addToCart(CartRequestDto dto);



&nbsp;   CartResponseDto getCartByUserId(Long userId);



&nbsp;   CartResponseDto updateCartItemQuantity(Long userId, Long productId, Integer quantity);



&nbsp;   void removeItemFromCart(Long userId, Long productId);



&nbsp;   void clearCart(Long userId);

}

✅ 6. CartServiceImpl (Complete Service Layer)



📌 File: src/main/java/com/advann/cart\_service/service/serviceImpl/CartServiceImpl.java



package com.advann.cart\_service.service.serviceImpl;



import com.advann.cart\_service.client.ProductClient;

import com.advann.cart\_service.dto.\*;

import com.advann.cart\_service.entity.Cart;

import com.advann.cart\_service.entity.CartItem;

import com.advann.cart\_service.exceptions.ResourceNotFoundException;

import com.advann.cart\_service.payload.ApiResponse;

import com.advann.cart\_service.repository.CartItemRepository;

import com.advann.cart\_service.repository.CartRepository;

import com.advann.cart\_service.service.services.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;



import java.math.BigDecimal;

import java.util.List;



@Service

@RequiredArgsConstructor

public class CartServiceImpl implements CartService {



&nbsp;   private final CartRepository cartRepository;

&nbsp;   private final CartItemRepository cartItemRepository;

&nbsp;   private final ProductClient productClient;



&nbsp;   @Override

&nbsp;   public CartResponseDto addToCart(CartRequestDto dto) {



&nbsp;       // fetch product from product-service

&nbsp;       ApiResponse<ProductResponseDto> productResponse = productClient.getProductById(dto.getProductId());



&nbsp;       if (productResponse == null || productResponse.getData() == null) {

&nbsp;           throw new ResourceNotFoundException("Product not found with id: " + dto.getProductId());

&nbsp;       }



&nbsp;       ProductResponseDto product = productResponse.getData();



&nbsp;       // find or create cart

&nbsp;       Cart cart = cartRepository.findByUserId(dto.getUserId())

&nbsp;               .orElseGet(() -> cartRepository.save(

&nbsp;                       Cart.builder()

&nbsp;                               .userId(dto.getUserId())

&nbsp;                               .build()

&nbsp;               ));



&nbsp;       // check if item already exists

&nbsp;       CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), dto.getProductId())

&nbsp;               .orElse(null);



&nbsp;       if (existingItem != null) {

&nbsp;           existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());

&nbsp;           existingItem.setPrice(product.getPrice());

&nbsp;           cartItemRepository.save(existingItem);

&nbsp;       } else {

&nbsp;           CartItem newItem = CartItem.builder()

&nbsp;                   .cart(cart)

&nbsp;                   .productId(dto.getProductId())

&nbsp;                   .quantity(dto.getQuantity())

&nbsp;                   .price(product.getPrice())

&nbsp;                   .build();



&nbsp;           cartItemRepository.save(newItem);

&nbsp;       }



&nbsp;       return getCartByUserId(dto.getUserId());

&nbsp;   }



&nbsp;   @Override

&nbsp;   public CartResponseDto getCartByUserId(Long userId) {



&nbsp;       Cart cart = cartRepository.findByUserId(userId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));



&nbsp;       List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());



&nbsp;       List<CartItemResponseDto> responseItems = cartItems.stream()

&nbsp;               .map(item -> {



&nbsp;                   ApiResponse<ProductResponseDto> productResponse = productClient.getProductById(item.getProductId());

&nbsp;                   ProductResponseDto product = productResponse.getData();



&nbsp;                   return CartItemResponseDto.builder()

&nbsp;                           .id(item.getId())

&nbsp;                           .productId(item.getProductId())

&nbsp;                           .productName(product != null ? product.getName() : null)

&nbsp;                           .productImage(product != null ? product.getImageUrl() : null)

&nbsp;                           .price(item.getPrice())

&nbsp;                           .quantity(item.getQuantity())

&nbsp;                           .totalPrice(item.getTotalPrice())

&nbsp;                           .build();

&nbsp;               })

&nbsp;               .toList();



&nbsp;       BigDecimal grandTotal = responseItems.stream()

&nbsp;               .map(CartItemResponseDto::getTotalPrice)

&nbsp;               .reduce(BigDecimal.ZERO, BigDecimal::add);



&nbsp;       int totalItems = responseItems.stream()

&nbsp;               .mapToInt(CartItemResponseDto::getQuantity)

&nbsp;               .sum();



&nbsp;       return CartResponseDto.builder()

&nbsp;               .cartId(cart.getId())

&nbsp;               .userId(cart.getUserId())

&nbsp;               .items(responseItems)

&nbsp;               .grandTotal(grandTotal)

&nbsp;               .totalItems(totalItems)

&nbsp;               .build();

&nbsp;   }



&nbsp;   @Override

&nbsp;   public CartResponseDto updateCartItemQuantity(Long userId, Long productId, Integer quantity) {



&nbsp;       Cart cart = cartRepository.findByUserId(userId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));



&nbsp;       CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for productId: " + productId));



&nbsp;       if (quantity <= 0) {

&nbsp;           cartItemRepository.delete(cartItem);

&nbsp;       } else {

&nbsp;           cartItem.setQuantity(quantity);

&nbsp;           cartItemRepository.save(cartItem);

&nbsp;       }



&nbsp;       return getCartByUserId(userId);

&nbsp;   }



&nbsp;   @Override

&nbsp;   public void removeItemFromCart(Long userId, Long productId) {



&nbsp;       Cart cart = cartRepository.findByUserId(userId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));



&nbsp;       CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for productId: " + productId));



&nbsp;       cartItemRepository.delete(cartItem);

&nbsp;   }



&nbsp;   @Override

&nbsp;   public void clearCart(Long userId) {



&nbsp;       Cart cart = cartRepository.findByUserId(userId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));



&nbsp;       cartItemRepository.deleteByCartId(cart.getId());

&nbsp;   }

}

✅ Important Note (Product Price Sync)



When user adds to cart, we store product price in cart-service DB.

So even if price changes later, cart keeps old price (good practice).

