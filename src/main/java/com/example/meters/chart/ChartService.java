package com.example.meters.chart;

import com.example.meters.dto.ChartResponse;

public interface ChartService {
    ChartResponse getReadingChart(Long meterId, Period period);
    ChartResponse getConsumptionChart(Long meterId, Period period);
}
