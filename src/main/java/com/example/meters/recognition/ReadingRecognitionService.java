package com.example.meters.recognition;

import org.springframework.web.multipart.MultipartFile;

public interface ReadingRecognitionService {
    int recognize(MultipartFile file);
}
