package com.example.meters.controller;

import com.example.meters.chart.ChartService;
import com.example.meters.chart.Period;
import com.example.meters.dto.ChartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/meters/{meterId}/chart")
@RequiredArgsConstructor
@Tag(name = "Charts", description = "Meter readings charts and consumption visualization")
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/readings")
    @Operation(
        summary = "Get readings chart",
        description = "Retrieve chart data for meter readings with aggregation"
    )
    public ResponseEntity<ChartResponse> getReadingChart(
            @PathVariable Long meterId,
            @RequestParam(defaultValue = "MONTH") Period period) {
        ChartResponse chart = chartService.getReadingChart(meterId, period);
        return ResponseEntity.ok(chart);
    }

    @GetMapping("/consumption")
    @Operation(
        summary = "Get consumption chart",
        description = "Retrieve chart data for meter consumption with aggregation"
    )
    public ResponseEntity<ChartResponse> getConsumptionChart(
            @PathVariable Long meterId,
            @RequestParam(defaultValue = "MONTH") Period period) {
        ChartResponse chart = chartService.getConsumptionChart(meterId, period);
        return ResponseEntity.ok(chart);
    }
}
