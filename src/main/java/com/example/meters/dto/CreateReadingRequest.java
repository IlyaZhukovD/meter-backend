package com.example.meters.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateReadingRequest {

    @NotNull
    private Long meterId;

    private LocalDate createdAt;

    @Min(0)
    private Integer value;
}
