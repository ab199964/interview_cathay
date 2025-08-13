package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateApiRequest;
import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.exception.ApiException;
import com.example.testinterview.repository.ExchangeRateRepository;
import com.example.testinterview.response.ApiResponseCode;
import com.example.testinterview.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Override
    public Mono<List<ExchangeRateDto>> getExchangeRateHistory(ExchangeRateApiRequest exchangeRateRequest) {
        return Mono.just(exchangeRateRequest)
                .flatMap(req -> {
                    LocalDate startDate;
                    LocalDate endDate;
                    try {
                        startDate = LocalDate.parse(req.getStartDate(), requestFormatter);
                        endDate = LocalDate.parse(req.getEndDate(), requestFormatter);
                    } catch (DateTimeParseException e) {
                        log.error("日期格式錯誤: {}", e.getMessage());
                        return Mono.error(new ApiException(ApiResponseCode.INVALID_DATE_RANGE));
                    }

                    LocalDate oneYearAgo = LocalDate.now().minusYears(1);
                    LocalDate yesterday = LocalDate.now().minusDays(1);

                    if (startDate.isBefore(oneYearAgo) || endDate.isAfter(yesterday) || startDate.isAfter(endDate)) {
                        log.warn("日期區間驗證失敗: {} ~ {}", startDate, endDate);
                        return Mono.error(new ApiException(ApiResponseCode.INVALID_DATE_RANGE));
                    }

                    String startStr = startDate.format(dbFormatter);
                    String endStr = endDate.format(dbFormatter);
                    return findAndMapRates(startStr, endStr);
                });
    }
    private Mono<List<ExchangeRateDto>> findAndMapRates(String startDateStr, String endDateStr) {
        return exchangeRateRepository.findByDateBetween(startDateStr, endDateStr)
                .map(rate -> {
                    ExchangeRateDto dto = new ExchangeRateDto();
                    dto.setDate(rate.getDate());
                    dto.setUsdToNtd(rate.getUsdToNtd().toString());
                    return dto;
                })
                .collectList();
    }

}