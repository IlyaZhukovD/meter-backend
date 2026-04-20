package com.example.meters.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateReadingRequest {

    @Min(0)
    @NotNull
    private Integer value;

    private LocalDate createdAt;
}
