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
        Product product = modelMapper.map(productRequestDto, Product.class);
        log.info("Adding new product : {}", productRequestDto.getName(), productRequestDto.getPrice(), productRequestDto.getQuantity());
        Product saveProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", saveProduct.getId());
        return modelMapper.map(saveProduct, ProductResponseDto.class);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));
        return modelMapper.map(product, ProductResponseDto.class);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existing.setName(productRequestDto.getName());
        existing.setPrice(productRequestDto.getPrice());
        existing.setQuantity(productRequestDto.getQuantity());

        Product updated = productRepository.save(existing);

        return modelMapper.map(updated, ProductResponseDto.class);
    }

    @Override
    public void deleteProduct(Long id) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(existing);
    }
}
