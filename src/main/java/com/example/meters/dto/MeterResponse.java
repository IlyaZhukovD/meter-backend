package com.example.meters.dto;

import com.example.meters.entity.MeterType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class MeterResponse {
    private Long id;
    private String name;
    private MeterType type;
    private Integer lastReadingValue;
    private LocalDateTime lastReadingDate;
    private Long userId;
}
