package com.example.meters.mapper;

import com.example.meters.dto.MeterResponse;
import com.example.meters.entity.Meter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeterMapper {
    @Mapping(source = "user.id", target = "userId")
    MeterResponse toResponse(Meter meter);
}
