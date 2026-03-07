package com.example.meters.controller;

import com.example.meters.dto.CreateReadingRequest;
import com.example.meters.dto.ReadingResponse;
import com.example.meters.dto.UpdateReadingRequest;
import com.example.meters.entity.Reading;
import com.example.meters.service.ReadingService;
import jakarta.validation.Valid;

import com.example.meters.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/readings")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Reading> createReading(
            @RequestParam Long meterId,
            @RequestParam Integer value,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        
        CreateReadingRequest request = new CreateReadingRequest();
        request.setMeterId(meterId);
        request.setValue(value);
        
        Reading reading = readingService.createReading(request, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reading);
    }

    @GetMapping
    public ResponseEntity<List<ReadingResponse>> getMeterReadings(
            @RequestParam Long meterId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ReadingResponse> readings = readingService.getMeterReadings(meterId, userId);
        return ResponseEntity.ok(readings);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Reading> updateReading(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReadingRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Reading reading = readingService.updateReading(id, request, userId);
        return ResponseEntity.ok(reading);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReading(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        readingService.deleteReading(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        String login = authentication.getName();
        return userService.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
