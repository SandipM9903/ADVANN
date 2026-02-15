We will implement AWS S3 image upload so your images will be stored in cloud, not in local folder.



✅ What we will do

Instead of saving image in:

uploads/products/



We will upload to AWS S3 and store URL like:



https://bucket-name.s3.ap-south-1.amazonaws.com/products/full/xxx.jpg

✅ Step 1: Create AWS S3 Bucket  (My recommendation is if you are new to aws s3 bucket first go through ***Advann\_AWS\_S3\_Setup\_Steps\_and\_Interview\_QA.pdf*** in my Interview Questions folder)



In AWS Console:



Search S3



Create Bucket



Name: advann-product-images



Region: ap-south-1 (Mumbai)



Disable "Block all public access" (if you want public image access)



Create



✅ Step 2: Add Dependency in pom.xml

<dependency>

&nbsp;   <groupId>software.amazon.awssdk</groupId>

&nbsp;   <artifactId>s3</artifactId>

&nbsp;   <version>2.25.62</version>

</dependency>

✅ Step 3: Add AWS Config in application.yml

aws:

&nbsp; s3:

&nbsp;   bucket-name: advann-product-images

&nbsp;   region: ap-south-1



&nbsp;   access-key: YOUR\_ACCESS\_KEY

&nbsp;   secret-key: YOUR\_SECRET\_KEY



⚠️ Later we will move keys to environment variables.



✅ Step 4: Create S3Config class



📌 com.advann.product\_service.config.S3Config



package com.advann.product\_service.config;



import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;



@Configuration

public class S3Config {



&nbsp;   @Value("${aws.s3.access-key}")

&nbsp;   private String accessKey;



&nbsp;   @Value("${aws.s3.secret-key}")

&nbsp;   private String secretKey;



&nbsp;   @Value("${aws.s3.region}")

&nbsp;   private String region;



&nbsp;   @Bean

&nbsp;   public S3Client s3Client() {



&nbsp;       AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);



&nbsp;       return S3Client.builder()

&nbsp;               .region(Region.of(region))

&nbsp;               .credentialsProvider(StaticCredentialsProvider.create(credentials))

&nbsp;               .build();

&nbsp;   }

}



✅ Step 5: Create S3Service class



📌 package com.advann.product\_service.service.services;



package com.advann.product\_service.service.services;



import org.springframework.web.multipart.MultipartFile;



public interface S3Service {



&nbsp;   String uploadFile(MultipartFile file, String folderName);



&nbsp;   String uploadBytes(byte\[] data, String contentType, String folderName);

}





✅ Step 6: Create S3ServiceImpl class



📌 package com.advann.product\_service.service.serviceImpl;



package com.advann.product\_service.service.serviceImpl;



import com.advann.product\_service.exceptions.InvalidFileException;

import com.advann.product\_service.service.services.S3Service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;



import java.io.IOException;

import java.util.UUID;



@Service

@RequiredArgsConstructor

public class S3ServiceImpl implements S3Service {



&nbsp;   private final S3Client s3Client;



&nbsp;   @Value("${aws.s3.bucket-name}")

&nbsp;   private String bucketName;



&nbsp;   @Value("${aws.s3.region}")

&nbsp;   private String region;



&nbsp;   @Override

&nbsp;   public String uploadFile(MultipartFile file, String folderName) {



&nbsp;       try {

&nbsp;           String fileName = UUID.randomUUID() + "\_" + file.getOriginalFilename();

&nbsp;           String key = folderName + "/" + fileName;



&nbsp;           PutObjectRequest request = PutObjectRequest.builder()

&nbsp;                   .bucket(bucketName)

&nbsp;                   .key(key)

&nbsp;                   .contentType(file.getContentType())

&nbsp;                   .build();



&nbsp;           s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));



&nbsp;           return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;



&nbsp;       } catch (IOException e) {

&nbsp;           throw new InvalidFileException("Failed to upload file to S3.");

&nbsp;       }

&nbsp;   }



&nbsp;   @Override

&nbsp;   public String uploadBytes(byte\[] data, String contentType, String folderName) {



&nbsp;       String fileName = UUID.randomUUID() + ".jpg";

&nbsp;       String key = folderName + "/" + fileName;



&nbsp;       PutObjectRequest request = PutObjectRequest.builder()

&nbsp;               .bucket(bucketName)

&nbsp;               .key(key)

&nbsp;               .contentType(contentType)

&nbsp;               .build();



&nbsp;       s3Client.putObject(request, RequestBody.fromBytes(data));



&nbsp;       return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;

&nbsp;   }

}





✅ Step 7: Create ProductServiceImpl class



📌 package com.advann.product\_service.service.serviceImpl;





@Override

&nbsp;   public List<ProductImageResponseDto> uploadProductImages(Long productId, List<MultipartFile> files) {



&nbsp;       Product product = productRepository.findById(productId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));



&nbsp;       if (files == null || files.isEmpty()) {

&nbsp;           throw new InvalidFileException("Please upload at least one image.");

&nbsp;       }



&nbsp;       long existingCount = productImageRepository.countByProductId(productId);



&nbsp;       if (existingCount + files.size() > MAX\_GALLERY\_IMAGES) {

&nbsp;           throw new InvalidFileException(

&nbsp;                   "Maximum " + MAX\_GALLERY\_IMAGES + " images allowed per product. Already uploaded: "

&nbsp;                           + existingCount + ", trying to upload: " + files.size()

&nbsp;           );

&nbsp;       }



&nbsp;       List<ProductImageResponseDto> responseList = new ArrayList<>();



&nbsp;       for (MultipartFile file : files) {



&nbsp;           validateImageFile(file);



&nbsp;           try {

&nbsp;               // ================= FULL IMAGE (800x800) =================

&nbsp;               ByteArrayOutputStream fullOutputStream = new ByteArrayOutputStream();



&nbsp;               Thumbnails.of(file.getInputStream())

&nbsp;                       .size(800, 800)

&nbsp;                       .outputQuality(0.8)

&nbsp;                       .toOutputStream(fullOutputStream);



&nbsp;               byte\[] fullBytes = fullOutputStream.toByteArray();



&nbsp;               // ================= THUMBNAIL IMAGE (300x300) =================

&nbsp;               ByteArrayOutputStream thumbOutputStream = new ByteArrayOutputStream();



&nbsp;               Thumbnails.of(file.getInputStream())

&nbsp;                       .size(300, 300)

&nbsp;                       .outputQuality(0.7)

&nbsp;                       .toOutputStream(thumbOutputStream);



&nbsp;               byte\[] thumbBytes = thumbOutputStream.toByteArray();



&nbsp;               // ================= UPLOAD TO S3 =================

&nbsp;               String fullImageUrl = s3Service.uploadBytes(fullBytes, file.getContentType(), "products/gallery/full");

&nbsp;               String thumbImageUrl = s3Service.uploadBytes(thumbBytes, file.getContentType(), "products/gallery/thumb");



&nbsp;               // ================= SAVE IN DB =================

&nbsp;               ProductImage productImage = ProductImage.builder()

&nbsp;                       .imagePath(fullImageUrl)

&nbsp;                       .thumbnailPath(thumbImageUrl)

&nbsp;                       .product(product)

&nbsp;                       .build();



&nbsp;               ProductImage savedImage = productImageRepository.save(productImage);



&nbsp;               responseList.add(ProductImageResponseDto.builder()

&nbsp;                       .id(savedImage.getId())

&nbsp;                       .imagePath(savedImage.getImagePath())

&nbsp;                       .imageUrl(savedImage.getImagePath())

&nbsp;                       .thumbnailPath(savedImage.getThumbnailPath())

&nbsp;                       .thumbnailUrl(savedImage.getThumbnailPath())

&nbsp;                       .build());



&nbsp;           } catch (Exception e) {

&nbsp;               throw new InvalidFileException("Failed to upload image to S3.");

&nbsp;           }

&nbsp;       }



&nbsp;       // ================= SET LAST UPLOADED AS PRIMARY =================

&nbsp;       if (!responseList.isEmpty()) {

&nbsp;           ProductImageResponseDto latestUploadedImage = responseList.get(responseList.size() - 1);



&nbsp;           product.setImagePath(latestUploadedImage.getImagePath());

&nbsp;           productRepository.save(product);



&nbsp;           log.info("Primary image updated to latest uploaded image for product id: {}", product.getId());

&nbsp;       }



&nbsp;       return responseList;

&nbsp;   }





@Override

&nbsp;   public ProductResponseDto uploadProductImage(Long productId, MultipartFile file) {



&nbsp;       Product product = productRepository.findById(productId)

&nbsp;               .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));



&nbsp;       validateImageFile(file);



&nbsp;       // Upload to S3

&nbsp;       String imageUrl = s3Service.uploadFile(file, "products/full");



&nbsp;       product.setImagePath(imageUrl);



&nbsp;       Product savedProduct = productRepository.save(product);



&nbsp;       ProductResponseDto dto = modelMapper.map(savedProduct, ProductResponseDto.class);

&nbsp;       dto.setImageUrl(savedProduct.getImagePath()); // because imagePath itself is full URL



&nbsp;       return dto;

&nbsp;   }









