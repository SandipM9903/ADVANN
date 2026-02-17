package com.advann.product_service.service.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String uploadFile(MultipartFile file, String folder);

    String uploadBytes(byte[] bytes, String contentType, String folder);

    void deleteFileByUrl(String fileUrl);

    String generatePresignedUrl(String fileUrl);
}