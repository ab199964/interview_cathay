package com.example.testinterview.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateDto {

    @JsonProperty("Date")
    private String date; // API 原始 yyyyMMdd

    @JsonProperty(value = "USD/NTD", access = JsonProperty.Access.WRITE_ONLY)
    private String usdToNtd;


    private LocalDateTime createdAt;

    // ===================================
    // 以下為額外欄位，非 API 原始資料 來自 request
    // ===================================

    private String endDate;

    private String startDate;

    // 這裡的 usd 是指 API 原始資料中的 "USD/NTD" 欄位
    private String usd;

    // 題目request設計上有點問題
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currency;

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

    @JsonProperty("usd")
    public String getUsd() {
        return usdToNtd;
    }
}
