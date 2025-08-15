package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.entity.ExchangeRate;
import com.example.testinterview.exception.ApiErrorException;
import com.example.testinterview.mapper.ExchangeRateMapper;
import com.example.testinterview.repository.ExchangeRateRepository;
import com.example.testinterview.response.ApiResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateMapper exchangeRateMapper;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private ExchangeRateDto requestDto;
    private ExchangeRate exchangeRate1;
    private ExchangeRate exchangeRate2;
    private ExchangeRateDto responseDto1;
    private ExchangeRateDto responseDto2;

    @BeforeEach
    void setUp() {
        // 設置請求對象
        requestDto = new ExchangeRateDto();
        requestDto.setCurrency("USD");
        requestDto.setStartDate("2025/07/01");
        requestDto.setEndDate("2025/07/31");

        // 設置數據庫實體對象
        exchangeRate1 = new ExchangeRate();
        exchangeRate1.setId("1");
        exchangeRate1.setDate(LocalDateTime.of(2025, 7, 10, 0, 0));
        exchangeRate1.setUsdToNtd(new BigDecimal("31.01"));
        exchangeRate1.setCreatedAt(LocalDateTime.of(2025, 7, 10, 8, 0));

        exchangeRate2 = new ExchangeRate();
        exchangeRate2.setId("2");
        exchangeRate2.setDate(LocalDateTime.of(2025, 7, 11, 0, 0));
        exchangeRate2.setUsdToNtd(new BigDecimal("31.02"));
        exchangeRate2.setCreatedAt(LocalDateTime.of(2025, 7, 11, 8, 0));

        // 設置響應DTO對象
        responseDto1 = new ExchangeRateDto();
        responseDto1.setDate("20250710");
        responseDto1.setUsdToNtd("31.01");

        responseDto2 = new ExchangeRateDto();
        responseDto2.setDate("20250711");
        responseDto2.setUsdToNtd("31.02");
    }

    @Test
    void getExchangeRateHistory_shouldReturnRates_whenValidRequest() {
        // 配置mock行為
        when(exchangeRateRepository.findUsdBetweenDates(any(), any()))
                .thenReturn(Flux.just(exchangeRate1, exchangeRate2));
        when(exchangeRateMapper.toDto(exchangeRate1)).thenReturn(responseDto1);
        when(exchangeRateMapper.toDto(exchangeRate2)).thenReturn(responseDto2);

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證結果
        StepVerifier.create(result)
                .expectNextMatches(list -> {
                    if (list.size() != 2) return false;

                    ExchangeRateDto dto1 = list.get(0);
                    ExchangeRateDto dto2 = list.get(1);

                    boolean dateCorrect = dto1.getDate().equals("20250710") && dto2.getDate().equals("20250711");
                    boolean usdToNtdCorrect = dto1.getUsdToNtd().equals("31.01") && dto2.getUsdToNtd().equals("31.02");

                    // 驗證 getUsd() 方法是否返回正確的值
                    boolean usdCorrect = dto1.getUsd().equals(dto1.getUsdToNtd()) && dto2.getUsd().equals(dto2.getUsdToNtd());

                    return dateCorrect && usdToNtdCorrect && usdCorrect;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenInvalidCurrency() {
        // 設置無效貨幣 (包含逗號)
        requestDto.setCurrency("USD,EUR");

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.INVALID_CURRENCY.getCode())
                )
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenCurrencyContainsSpace() {
        // 設置無效貨幣 (包含空格)
        requestDto.setCurrency("USD EUR");

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.INVALID_CURRENCY.getCode())
                )
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenEmptyCurrency() {
        // 設置無效貨幣 (空字符串)
        requestDto.setCurrency("");

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.INVALID_CURRENCY.getCode())
                )
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenStartDateAfterEndDate() {
        // 設置無效日期範圍 (開始日期晚於結束日期)
        requestDto.setStartDate("2025/07/31");
        requestDto.setEndDate("2025/07/01");

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.INVALID_DATE_RANGE.getCode())
                )
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenInvalidDateFormat() {
        // 設置無效日期格式
        requestDto.setStartDate("2025-07-01");

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出業務邏輯異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.BUSINESS_LOGIC.getCode())
                )
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenDateTooOld() {
        // 設置超出範圍的日期 (超過一年)
        LocalDate oneYearAgo = LocalDate.now().minusYears(1).minusDays(1);
        String oldDate = oneYearAgo.getYear() + "/" +
                         String.format("%02d", oneYearAgo.getMonthValue()) + "/" +
                         String.format("%02d", oneYearAgo.getDayOfMonth());

        requestDto.setStartDate(oldDate);
        requestDto.setEndDate("2025/07/31");

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.INVALID_DATE_RANGE.getCode())
                )
                .verify();
    }

    @Test
    void getExchangeRateHistory_shouldThrowException_whenDateInFuture() {
        // 設置未來日期 (明天)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String futureDate = tomorrow.getYear() + "/" +
                            String.format("%02d", tomorrow.getMonthValue()) + "/" +
                            String.format("%02d", tomorrow.getDayOfMonth());

        requestDto.setStartDate("2025/07/01");
        requestDto.setEndDate(futureDate);

        // 調用被測方法
        Mono<List<ExchangeRateDto>> result = exchangeRateService.getExchangeRateHistory(requestDto);

        // 驗證拋出異常 - 使用正確的錯誤代碼
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof ApiErrorException &&
                    ((ApiErrorException) throwable).getCode().equals(ApiResponseCode.INVALID_DATE_RANGE.getCode())
                )
                .verify();
    }
}
