package com.example.meters.dto;

import com.example.meters.entity.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMeterRequest {

    @NotBlank
    private String name;

    @NotNull
    private MeterType type;
}
