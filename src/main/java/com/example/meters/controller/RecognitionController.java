package com.example.meters.controller;

import com.example.meters.recognition.ReadingRecognitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/recognize")
@RequiredArgsConstructor
@Tag(name = "Recognition", description = "Meter reading recognition from photos")
public class RecognitionController {

    private final ReadingRecognitionService recognitionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Recognize meter reading",
        description = "Extract meter reading value from photo without saving to database (MVP implementation returns random value). Parameter: file (binary)"
    )
    public ResponseEntity<Map<String, Integer>> recognize(@RequestParam("file") MultipartFile file) {
        int value = recognitionService.recognize(file);
        return ResponseEntity.ok(Map.of("value", value));
    }
}
