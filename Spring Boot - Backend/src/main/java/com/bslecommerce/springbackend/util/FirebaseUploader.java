package com.bslecommerce.springbackend.util;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
public class FirebaseUploader {
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    @Value("${firebase.bucketName}")
    private String bucketName;

    public String uploadPhoto(MultipartFile file, String folderName) throws Exception {
        try {
            String fileName = UUID.randomUUID() + file.getOriginalFilename();
            storage.create(BlobInfo.newBuilder(bucketName, folderName + "/" + fileName).build(), file.getBytes());
            return "https://storage.googleapis.com/" + bucketName + "/" + folderName + "/" + fileName;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deletePhoto(String photoUrl, String folderName) throws Exception {
        try {
            String fileName = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
            BlobId blobId = BlobId.of(bucketName, folderName + "/" + fileName);
            storage.delete(blobId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
