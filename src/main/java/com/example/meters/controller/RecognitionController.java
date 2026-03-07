package com.example.meters.controller;

import com.example.meters.recognition.ReadingRecognitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/recognize")
@RequiredArgsConstructor
public class RecognitionController {

    private final ReadingRecognitionService recognitionService;

    @PostMapping
    public ResponseEntity<Map<String, Integer>> recognize(@RequestParam("file") MultipartFile file) {
        int value = recognitionService.recognize(file);
        return ResponseEntity.ok(Map.of("value", value));
    }
}
