package com.example.testinterview.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateDto {

    @JsonProperty("Date")
    private String date; // API 原始 yyyyMMdd

    @JsonProperty("USD/NTD")
    private String usdToNtd;

    private LocalDateTime createdAt;


    public boolean hasValidUsdToNtd() {
        if (usdToNtd == null || usdToNtd.isBlank()) return false;
        try {
            new java.math.BigDecimal(usdToNtd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDate toLocalDateOrNull() {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }
}
