package com.example.meters.service;

import com.example.meters.dto.CreateReadingRequest;
import com.example.meters.dto.ReadingResponse;
import com.example.meters.dto.UpdateReadingRequest;
import com.example.meters.entity.Reading;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

public interface ReadingService {
    Reading createReading(CreateReadingRequest request, MultipartFile file, Long userId);
    List<ReadingResponse> getMeterReadings(Long meterId, Long userId);
    Reading updateReading(Long readingId, UpdateReadingRequest request, Long userId);
    void deleteReading(Long readingId, Long userId);
    Reading getReadingById(Long readingId);
    InputStream getFile(String filename);
}
