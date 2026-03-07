package com.example.meters.controller;

import com.example.meters.dto.StatsResponse;
import com.example.meters.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/meters/{meterId}/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Meter statistics and analytics")
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    @Operation(
        summary = "Get meter statistics",
        description = "Retrieve statistical data for meter including consumption metrics"
    )
    public ResponseEntity<StatsResponse> getMeterStats(@PathVariable Long meterId) {
        StatsResponse stats = statsService.getMeterStats(meterId);
        return ResponseEntity.ok(stats);
    }
}
