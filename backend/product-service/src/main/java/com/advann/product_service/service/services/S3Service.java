package com.advann.product_service.service.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String uploadFile(MultipartFile file, String folderName);

    String uploadBytes(byte[] data, String contentType, String folderName);
}