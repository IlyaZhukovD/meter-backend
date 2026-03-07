package com.example.meters.recognition;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Random;

@Service
public class RandomRecognitionService implements ReadingRecognitionService {

    private final Random random = new Random();

    @Override
    public int recognize(MultipartFile file) {
        // In MVP, return random value between 0 and 9999
        return random.nextInt(10000);
    }
}
