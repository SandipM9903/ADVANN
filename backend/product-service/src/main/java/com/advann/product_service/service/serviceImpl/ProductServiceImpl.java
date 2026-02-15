package com.advann.product_service.service.serviceImpl;

import com.advann.product_service.dto.PagedResponseDto;
import com.advann.product_service.dto.ProductImageResponseDto;
import com.advann.product_service.dto.ProductRequestDto;
import com.advann.product_service.dto.ProductResponseDto;
import com.advann.product_service.entity.Category;
import com.advann.product_service.entity.Product;
import com.advann.product_service.entity.ProductImage;
import com.advann.product_service.exceptions.InvalidFileException;
import com.advann.product_service.exceptions.ResourceNotFoundException;
import com.advann.product_service.repository.CategoryRepository;
import com.advann.product_service.repository.ProductImageRepository;
import com.advann.product_service.repository.ProductRepository;
import com.advann.product_service.service.services.ProductService;
import com.advann.product_service.service.services.S3Service;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;
    @Value("${app.base-url}")
    private String baseUrl;
    private static final int MAX_GALLERY_IMAGES = 5;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {

        log.info("Creating new product with name: {}", productRequestDto.getName());

        Product product = modelMapper.map(productRequestDto, Product.class);

        product.setId(null);

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + productRequestDto.getCategoryId()
                ));

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());

        ProductResponseDto responseDto = modelMapper.map(savedProduct, ProductResponseDto.class);

        responseDto.setCategoryId(category.getId());
        responseDto.setCategoryName(category.getName());

        return responseDto;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {

        log.info("Fetching all products");

        List<Product> products = productRepository.findAll();

        log.info("Total products found: {}", products.size());

        return products.stream()
                .map(product -> {
                    ProductResponseDto dto = modelMapper.map(product, ProductResponseDto.class);
                    dto.setImageUrl(buildImageUrl(product.getImagePath()));
                    return dto;
                })
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

        ProductResponseDto dto = modelMapper.map(product, ProductResponseDto.class);

        dto.setImageUrl(buildImageUrl(product.getImagePath()));

        return dto;
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

        // ✅ delete image file if exists
        deleteOldImageIfExists(existing);

        // ✅ delete product from DB
        productRepository.delete(existing);

        log.info("Product deleted successfully with id: {}", id);
    }

    @Override
    public PagedResponseDto<ProductResponseDto> getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String keyword,
            Long categoryId
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;

        // ✅ category + keyword
        if (categoryId != null && keyword != null && !keyword.isBlank()) {
            productPage = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, keyword, pageable);
        }
        // ✅ only category filter
        else if (categoryId != null) {
            productPage = productRepository.findByCategoryId(categoryId, pageable);
        }
        // ✅ only keyword search
        else if (keyword != null && !keyword.isBlank()) {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        // ✅ normal pagination
        else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductResponseDto> products = productPage.getContent()
                .stream()
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .categoryId(product.getCategory().getId())
                        .categoryName(product.getCategory().getName())
                        .imagePath(product.getImagePath())
                        .imageUrl(buildImageUrl(product.getImagePath()))
                        .build()
                )
                .toList();

        return PagedResponseDto.<ProductResponseDto>builder()
                .content(products)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    @Override
    public ProductResponseDto uploadProductImage(Long productId, MultipartFile file) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        validateImageFile(file);

        // Upload to S3
        String imageUrl = s3Service.uploadFile(file, "products/full");

        product.setImagePath(imageUrl);

        Product savedProduct = productRepository.save(product);

        ProductResponseDto dto = modelMapper.map(savedProduct, ProductResponseDto.class);
        dto.setImageUrl(savedProduct.getImagePath()); // because imagePath itself is full URL

        return dto;
    }

    private void validateImageFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is required. Please upload a file.");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png"))) {
            throw new InvalidFileException("Only JPG and PNG images are allowed");
        }

        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("File size must be less than 2MB");
        }
    }

    private void deleteOldImageIfExists(Product product) {

        if (product.getImagePath() == null || product.getImagePath().isBlank()) {
            log.info("No image found for product id: {}", product.getId());
            return;
        }

        String fullPath = System.getProperty("user.dir") + product.getImagePath().replace("/", File.separator);

        try {
            boolean deleted = Files.deleteIfExists(Paths.get(fullPath));

            if (deleted) {
                log.info("Image deleted successfully: {}", fullPath);
            } else {
                log.warn("Image file not found, nothing to delete: {}", fullPath);
            }

        } catch (IOException e) {
            log.error("Failed to delete image file: {}", fullPath, e);
            throw new InvalidFileException("Failed to delete product image from server.");
        }
    }

    private String buildImageUrl(String imagePath) {

        if (imagePath == null || imagePath.isBlank()) {
            return baseUrl + "/images/default.jpg";
        }

        return baseUrl + imagePath;
    }

    @Override
    public ProductResponseDto deleteProductImage(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getImagePath() == null || product.getImagePath().isBlank()) {
            throw new InvalidFileException("No image found for this product.");
        }

        String fullPath = System.getProperty("user.dir") + product.getImagePath().replace("/", File.separator);

        File file = new File(fullPath);

        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new InvalidFileException("Failed to delete product image.");
            }
        }

        product.setImagePath(null);

        Product savedProduct = productRepository.save(product);

        ProductResponseDto dto = modelMapper.map(savedProduct, ProductResponseDto.class);
        dto.setImageUrl(null);

        return dto;
    }

    @Override
    public ProductResponseDto updateProductImage(Long productId, MultipartFile file) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        validateImageFile(file);

        // delete old image if exists
        deleteOldImageIfExists(product);

        String uploadDir = System.getProperty("user.dir") + "/uploads/products/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new InvalidFileException("Invalid file name.");
        }

        originalFileName = originalFileName.replaceAll("\\s+", "_");

        // Extract extension
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        String fileName = System.currentTimeMillis() + "_" + productId + extension;

        Path filePath = Paths.get(uploadDir).resolve(fileName);

        try {
            Thumbnails.of(file.getInputStream())
                    .size(800, 800)
                    .outputQuality(0.7)
                    .toFile(filePath.toFile());
        } catch (IOException e) {
            throw new InvalidFileException("Failed to update product image.");
        }

        String imagePath = "/uploads/products/" + fileName;
        product.setImagePath(imagePath);

        Product savedProduct = productRepository.save(product);

        ProductResponseDto dto = modelMapper.map(savedProduct, ProductResponseDto.class);
        dto.setImageUrl(buildImageUrl(savedProduct.getImagePath()));

        return dto;
    }

    @Override
    public List<ProductImageResponseDto> uploadProductImages(Long productId, List<MultipartFile> files) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("Please upload at least one image.");
        }

        long existingCount = productImageRepository.countByProductId(productId);

        if (existingCount + files.size() > MAX_GALLERY_IMAGES) {
            throw new InvalidFileException(
                    "Maximum " + MAX_GALLERY_IMAGES + " images allowed per product. Already uploaded: "
                            + existingCount + ", trying to upload: " + files.size()
            );
        }

        List<ProductImageResponseDto> responseList = new ArrayList<>();

        for (MultipartFile file : files) {

            validateImageFile(file);

            try {
                // ================= FULL IMAGE (800x800) =================
                ByteArrayOutputStream fullOutputStream = new ByteArrayOutputStream();

                Thumbnails.of(file.getInputStream())
                        .size(800, 800)
                        .outputQuality(0.8)
                        .toOutputStream(fullOutputStream);

                byte[] fullBytes = fullOutputStream.toByteArray();

                // ================= THUMBNAIL IMAGE (300x300) =================
                ByteArrayOutputStream thumbOutputStream = new ByteArrayOutputStream();

                Thumbnails.of(file.getInputStream())
                        .size(300, 300)
                        .outputQuality(0.7)
                        .toOutputStream(thumbOutputStream);

                byte[] thumbBytes = thumbOutputStream.toByteArray();

                // ================= UPLOAD TO S3 =================
                String fullImageUrl = s3Service.uploadBytes(fullBytes, file.getContentType(), "products/gallery/full");
                String thumbImageUrl = s3Service.uploadBytes(thumbBytes, file.getContentType(), "products/gallery/thumb");

                // ================= SAVE IN DB =================
                ProductImage productImage = ProductImage.builder()
                        .imagePath(fullImageUrl)
                        .thumbnailPath(thumbImageUrl)
                        .product(product)
                        .build();

                ProductImage savedImage = productImageRepository.save(productImage);

                responseList.add(ProductImageResponseDto.builder()
                        .id(savedImage.getId())
                        .imagePath(savedImage.getImagePath())
                        .imageUrl(savedImage.getImagePath())
                        .thumbnailPath(savedImage.getThumbnailPath())
                        .thumbnailUrl(savedImage.getThumbnailPath())
                        .build());

            } catch (Exception e) {
                throw new InvalidFileException("Failed to upload image to S3.");
            }
        }

        // ================= SET LAST UPLOADED AS PRIMARY =================
        if (!responseList.isEmpty()) {
            ProductImageResponseDto latestUploadedImage = responseList.get(responseList.size() - 1);

            product.setImagePath(latestUploadedImage.getImagePath());
            productRepository.save(product);

            log.info("Primary image updated to latest uploaded image for product id: {}", product.getId());
        }

        return responseList;
    }
    @Override
    public List<ProductImageResponseDto> getProductImages(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        List<ProductImage> images = productImageRepository.findByProductId(productId);

        return images.stream()
                .map(img -> ProductImageResponseDto.builder()
                        .id(img.getId())
                        .imagePath(img.getImagePath())
                        .imageUrl(buildImageUrl(img.getImagePath()))
                        .thumbnailPath(img.getThumbnailPath())
                        .thumbnailUrl(buildImageUrl(img.getThumbnailPath()))
                        .build())
                .toList();
    }

    @Override
    public void deleteProductImageById(Long imageId) {

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        Product product = productImage.getProduct();

        boolean isPrimary = product.getImagePath() != null &&
                product.getImagePath().equals(productImage.getImagePath());

        String fullPath = System.getProperty("user.dir") + productImage.getImagePath().replace("/", File.separator);

        try {
            boolean deleted = Files.deleteIfExists(Paths.get(fullPath));

            if (deleted) {
                log.info("Gallery image deleted from folder: {}", fullPath);
            } else {
                log.warn("Gallery image file not found, skipping delete: {}", fullPath);
            }

        } catch (IOException e) {
            log.error("Failed to delete gallery image file: {}", fullPath, e);
            throw new InvalidFileException("Failed to delete image from server.");
        }

        // delete image record
        productImageRepository.delete(productImage);
        log.info("Gallery image deleted from DB with id: {}", imageId);

        // if deleted image was primary image
        if (isPrimary) {

            List<ProductImage> remainingImages = productImageRepository.findByProductIdOrderByCreatedAtDesc(product.getId());

            if (!remainingImages.isEmpty()) {
                // set first remaining image as new primary
                product.setImagePath(remainingImages.get(0).getImagePath());
                log.info("Primary image updated to another gallery image for product id: {}", product.getId());
            } else {
                // no images left, set null (default will come)
                product.setImagePath(null);
                log.info("No gallery images left. Primary image reset to null for product id: {}", product.getId());
            }

            productRepository.save(product);
        }
    }

    @Override
    public ProductResponseDto setPrimaryProductImage(Long imageId) {

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        Product product = productImage.getProduct();

        // set main image path
        product.setImagePath(productImage.getImagePath());

        Product savedProduct = productRepository.save(product);

        ProductResponseDto dto = modelMapper.map(savedProduct, ProductResponseDto.class);
        dto.setImageUrl(buildImageUrl(savedProduct.getImagePath()));

        return dto;
    }

    @Override
    public PagedResponseDto<ProductImageResponseDto> getProductImagesWithPagination(Long productId, int page, int size, String sortDir) {

        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductImage> imagePage = productImageRepository.findByProductId(productId, pageable);

        List<ProductImageResponseDto> images = imagePage.getContent()
                .stream()
                .map(img -> ProductImageResponseDto.builder()
                        .id(img.getId())
                        .imagePath(img.getImagePath())
                        .imageUrl(buildImageUrl(img.getImagePath()))
                        .thumbnailPath(img.getThumbnailPath())
                        .thumbnailUrl(buildImageUrl(img.getThumbnailPath()))
                        .build())
                .toList();

        return PagedResponseDto.<ProductImageResponseDto>builder()
                .content(images)
                .pageNumber(imagePage.getNumber())
                .pageSize(imagePage.getSize())
                .totalElements(imagePage.getTotalElements())
                .totalPages(imagePage.getTotalPages())
                .last(imagePage.isLast())
                .build();
    }
}
