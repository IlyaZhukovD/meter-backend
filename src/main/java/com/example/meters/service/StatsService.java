package com.example.meters.service;

import com.example.meters.dto.StatsResponse;

public interface StatsService {
    StatsResponse getMeterStats(Long meterId);
}
