package com.example.meters.service;

import com.example.meters.dto.CreateMeterRequest;
import com.example.meters.dto.MeterResponse;
import com.example.meters.dto.UpdateMeterRequest;
import com.example.meters.entity.Meter;
import java.util.List;

public interface MeterService {
    Meter createMeter(CreateMeterRequest request, Long userId);
    List<MeterResponse> getUserMeters(Long userId);
    Meter updateMeter(Long meterId, UpdateMeterRequest request, Long userId);
    Meter getMeterById(Long meterId);
}
