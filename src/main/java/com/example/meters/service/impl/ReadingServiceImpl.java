package com.example.meters.service.impl;

import com.example.meters.dto.CreateReadingRequest;
import com.example.meters.dto.ReadingResponse;
import com.example.meters.dto.UpdateReadingRequest;
import com.example.meters.entity.Meter;
import com.example.meters.entity.Reading;
import com.example.meters.exception.ResourceNotFoundException;
import com.example.meters.exception.UnauthorizedException;
import com.example.meters.mapper.ReadingMapper;
import com.example.meters.repository.MeterRepository;
import com.example.meters.repository.ReadingRepository;
import com.example.meters.service.MeterService;
import com.example.meters.service.ReadingService;
import com.example.meters.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;
    private final MeterRepository meterRepository;
    private final MeterService meterService;
    private final FileStorageService fileStorageService;
    private final ReadingMapper readingMapper;

    @Override
    public Reading createReading(CreateReadingRequest request, MultipartFile file, Long userId) {
        // Validate that the meter belongs to the user
        Meter meter = meterService.getMeterById(request.getMeterId());
        if (!meter.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to add reading to this meter");
        }

        // Upload file to storage
        String photoUrl = fileStorageService.upload(file);

        // Create reading
        Reading reading = new Reading();
        reading.setMeter(meter);
        reading.setValue(request.getValue());
        reading.setPhotoUrl(photoUrl);
        reading.setCreatedAt(LocalDateTime.of(request.getCreatedAt(), LocalDateTime.now().toLocalTime()));

        return readingRepository.save(reading);
    }

    @Override
    public List<ReadingResponse> getMeterReadings(Long meterId, Long userId) {
        // Validate that the meter belongs to the user
        Meter meter = meterService.getMeterById(meterId);
        if (!meter.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to view readings for this meter");
        }

        List<Reading> readings = readingRepository.findByMeterIdAndDeletedAtIsNullOrderByCreatedAtDesc(meterId);
        return readings.stream()
                .map(readingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Reading updateReading(Long readingId, UpdateReadingRequest request, Long userId) {
        Reading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found with id: " + readingId));

        // Validate that the reading belongs to the user
        if (!reading.getMeter().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to update this reading");
        }

        reading.setValue(request.getValue());
        reading.setUpdatedAt(LocalDateTime.now());

        return readingRepository.save(reading);
    }

    @Override
    public void deleteReading(Long readingId, Long userId) {
        Reading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found with id: " + readingId));

        // Validate that the reading belongs to the user
        if (!reading.getMeter().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to delete this reading");
        }

        reading.setDeletedAt(LocalDateTime.now());
        readingRepository.save(reading);
    }

    @Override
    public Reading getReadingById(Long readingId) {
        return readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found with id: " + readingId));
    }

    @Override
    public InputStream getFile(String filename) {
        return fileStorageService.getFile(filename);
    }
}
