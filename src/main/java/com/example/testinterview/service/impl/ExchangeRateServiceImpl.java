package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.mapper.ExchangeRateMapper;
import com.example.testinterview.repository.ExchangeRateRepository;
import com.example.testinterview.response.ApiErrorException;
import com.example.testinterview.response.ApiResponseCode;
import com.example.testinterview.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;

    @Override
    public Mono<List<ExchangeRateDto>> getExchangeRateHistory(ExchangeRateDto request) {
        String currency = request.getCurrency();

        // 幣別檢查
        if (currency == null || currency.isBlank() || currency.contains(",") || currency.contains(" ")) {
            return Mono.error(new ApiErrorException(ApiResponseCode.INVALID_CURRENCY.getCode(),
                    ApiResponseCode.INVALID_CURRENCY.getMessage()));
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);
            LocalDate today = LocalDate.now();

            // 日期範圍檢查
            if (startDate.isBefore(today.minusYears(1)) || endDate.isAfter(today.minusDays(1)) || startDate.isAfter(endDate)) {
                return Mono.error(new ApiErrorException(ApiResponseCode.INVALID_DATE_RANGE.getCode(),
                        ApiResponseCode.INVALID_DATE_RANGE.getMessage()));
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atStartOfDay();

            return exchangeRateRepository.findUsdBetweenDates(startDateTime, endDateTime)
                    .map(exchangeRateMapper::toDto)
                    .collectList();

        } catch (Exception e) {
            return Mono.error(new ApiErrorException(ApiResponseCode.BUSINESS_LOGIC.getCode(),
                    ApiResponseCode.BUSINESS_LOGIC.getMessage()));
        }
    }
}

