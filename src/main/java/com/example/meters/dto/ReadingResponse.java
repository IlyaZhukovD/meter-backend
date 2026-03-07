package com.example.meters.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReadingResponse {
    private Long id;
    private Integer value;
    private String photoUrl;
    private LocalDateTime createdAt;
    private Long meterId;
}
