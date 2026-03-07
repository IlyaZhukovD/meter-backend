package com.example.meters.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {
    String upload(MultipartFile file);
    InputStream getFile(String filename);
}
