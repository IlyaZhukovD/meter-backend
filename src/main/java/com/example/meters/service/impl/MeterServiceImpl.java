package com.example.meters.service.impl;

import com.example.meters.dto.CreateMeterRequest;
import com.example.meters.dto.MeterResponse;
import com.example.meters.dto.UpdateMeterRequest;
import com.example.meters.entity.Meter;
import com.example.meters.entity.User;
import com.example.meters.exception.ResourceNotFoundException;
import com.example.meters.exception.UnauthorizedException;
import com.example.meters.mapper.MeterMapper;
import com.example.meters.repository.MeterRepository;
import com.example.meters.repository.ReadingRepository;
import com.example.meters.service.MeterService;
import com.example.meters.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final UserService userService;
    private final ReadingRepository readingRepository;
    private final MeterMapper meterMapper;

    @Override
    public Meter createMeter(CreateMeterRequest request, Long userId) {
        User user = userService.getUserById(userId);
        
        Meter meter = new Meter();
        meter.setName(request.getName());
        meter.setType(request.getType());
        meter.setUser(user);
        meter.setCreatedAt(LocalDateTime.now());
        
        return meterRepository.save(meter);
    }

    @Override
    public List<MeterResponse> getUserMeters(Long userId) {
        List<Meter> meters = meterRepository.findByUserId(userId);
        
        return meters.stream()
                .map(meter -> {
                    MeterResponse response = meterMapper.toResponse(meter);
                    
                    // Get the latest reading for this meter
                    readingRepository.findByMeterIdAndDeletedAtIsNullOrderByCreatedAtDesc(meter.getId())
                            .stream()
                            .findFirst()
                            .ifPresent(reading -> {
                                response.setLastReadingValue(reading.getValue());
                                response.setLastReadingDate(reading.getCreatedAt());
                            });
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Meter updateMeter(Long meterId, UpdateMeterRequest request, Long userId) {
        Meter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + meterId));
        
        // Check if the meter belongs to the user
        if (!meter.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to update this meter");
        }
        
        meter.setName(request.getName());
        meter.setType(request.getType());
        meter.setUpdatedAt(LocalDateTime.now());
        
        return meterRepository.save(meter);
    }

    @Override
    public Meter getMeterById(Long meterId) {
        return meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + meterId));
    }
}
