package com.example.meters.controller;

import com.example.meters.dto.StatsResponse;
import com.example.meters.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meters/{meterId}/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<StatsResponse> getMeterStats(@PathVariable Long meterId) {
        StatsResponse stats = statsService.getMeterStats(meterId);
        return ResponseEntity.ok(stats);
    }
}
