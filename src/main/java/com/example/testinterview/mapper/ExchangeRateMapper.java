package com.example.testinterview.mapper;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {

    @Mapping(target = "date", expression = "java(toLocalDateTime(dto.getDate()))")
    ExchangeRate toEntity(ExchangeRateDto dto);

    default LocalDateTime toLocalDateTime(String date) {
        if (date == null || date.isBlank()) return null;
        LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return ld.atStartOfDay(); // 補 00:00:00
    }
}
