package com.example.meters.mapper;

import com.example.meters.dto.ReadingResponse;
import com.example.meters.entity.Reading;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadingMapper {
    @Mapping(source = "meter.id", target = "meterId")
    ReadingResponse toResponse(Reading reading);
}
