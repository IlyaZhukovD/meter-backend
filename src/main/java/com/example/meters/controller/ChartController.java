package com.example.meters.controller;

import com.example.meters.chart.ChartService;
import com.example.meters.chart.Period;
import com.example.meters.dto.ChartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meters/{meterId}/chart")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/readings")
    public ResponseEntity<ChartResponse> getReadingChart(
            @PathVariable Long meterId,
            @RequestParam(defaultValue = "MONTH") Period period) {
        ChartResponse chart = chartService.getReadingChart(meterId, period);
        return ResponseEntity.ok(chart);
    }

    @GetMapping("/consumption")
    public ResponseEntity<ChartResponse> getConsumptionChart(
            @PathVariable Long meterId,
            @RequestParam(defaultValue = "MONTH") Period period) {
        ChartResponse chart = chartService.getConsumptionChart(meterId, period);
        return ResponseEntity.ok(chart);
    }
}
