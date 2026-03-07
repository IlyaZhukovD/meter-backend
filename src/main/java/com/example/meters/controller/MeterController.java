package com.example.meters.controller;

import com.example.meters.dto.CreateMeterRequest;
import com.example.meters.dto.MeterResponse;
import com.example.meters.dto.UpdateMeterRequest;
import com.example.meters.entity.Meter;
import com.example.meters.service.MeterService;
import jakarta.validation.Valid;

import com.example.meters.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/meters")
@RequiredArgsConstructor
@Tag(name = "Meters", description = "Water meter management endpoints")
public class MeterController {

    private final MeterService meterService;
    private final UserService userService;

    @PostMapping
    @Operation(
        summary = "Create new meter",
        description = "Create a new water meter for the authenticated user"
    )
    public ResponseEntity<Meter> createMeter(
            @Valid @RequestBody CreateMeterRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Meter meter = meterService.createMeter(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(meter);
    }

    @GetMapping
    @Operation(
        summary = "Get user meters",
        description = "Retrieve all meters belonging to the authenticated user"
    )
    public ResponseEntity<List<MeterResponse>> getUserMeters(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<MeterResponse> meters = meterService.getUserMeters(userId);
        return ResponseEntity.ok(meters);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update meter",
        description = "Update an existing meter belonging to the authenticated user"
    )
    public ResponseEntity<Meter> updateMeter(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMeterRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Meter meter = meterService.updateMeter(id, request, userId);
        return ResponseEntity.ok(meter);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        String login = authentication.getName();
        return userService.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
