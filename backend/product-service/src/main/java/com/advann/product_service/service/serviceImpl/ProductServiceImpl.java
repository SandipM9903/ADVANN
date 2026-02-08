package com.advann.product_service.service.serviceImpl;

import com.advann.product_service.dto.ProductRequestDto;
import com.advann.product_service.dto.ProductResponseDto;
import com.advann.product_service.entity.Product;
import com.advann.product_service.exceptions.ResourceNotFoundException;
import com.advann.product_service.repository.ProductRepository;
import com.advann.product_service.service.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {

        log.info("Creating new product with name: {}", productRequestDto.getName());

        Product product = modelMapper.map(productRequestDto, Product.class);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());

        return modelMapper.map(savedProduct, ProductResponseDto.class);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {

        log.info("Fetching all products");

        List<Product> products = productRepository.findAll();

        log.info("Total products found: {}", products.size());

        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {

        log.info("Fetching product by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        log.info("Product found with id: {}", id);

        return modelMapper.map(product, ProductResponseDto.class);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {

        log.info("Updating product with id: {}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update. Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        existing.setName(productRequestDto.getName());
        existing.setPrice(productRequestDto.getPrice());
        existing.setQuantity(productRequestDto.getQuantity());

        Product updated = productRepository.save(existing);

        log.info("Product updated successfully with id: {}", updated.getId());

        return modelMapper.map(updated, ProductResponseDto.class);
    }

    @Override
    public void deleteProduct(Long id) {

        log.info("Deleting product with id: {}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete. Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        productRepository.delete(existing);

        log.info("Product deleted successfully with id: {}", id);
    }
}
