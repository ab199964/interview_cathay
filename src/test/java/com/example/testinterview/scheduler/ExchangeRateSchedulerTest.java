package com.example.testinterview.scheduler;

import com.example.testinterview.entity.ExchangeRate;
import com.example.testinterview.repository.ExchangeRateRepository;
import com.example.testinterview.service.ExchangeRateApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateSchedulerTest {

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateScheduler exchangeRateScheduler;

    private ExchangeRate testRate1;
    private ExchangeRate testRate2;

    @BeforeEach
    void setUp() {
        testRate1 = ExchangeRate.builder()
                .id("1")
                .date("20250725")
                .usdToNtd(new BigDecimal("29.46"))
                .createdAt(LocalDateTime.now())
                .build();

        testRate2 = ExchangeRate.builder()
                .id("2")
                .date("20250726")
                .usdToNtd(new BigDecimal("29.50"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testFetchDailyRate_Success() {

        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.just(testRate1, testRate2));
        when(exchangeRateRepository.save(any(ExchangeRate.class)))
                .thenReturn(Mono.just(testRate1))
                .thenReturn(Mono.just(testRate2));
        exchangeRateScheduler.fetchDailyRate();
        verify(exchangeRateApiClient, times(1)).fetchUsdToNtdRates();
        verify(exchangeRateRepository, times(2)).save(any(ExchangeRate.class));
    }

    @Test
    void testFetchDailyRate_NoData() {
        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.empty());
        exchangeRateScheduler.fetchDailyRate();
        verify(exchangeRateApiClient, times(1)).fetchUsdToNtdRates();
        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    void testFetchDailyRate_ErrorHandling() {
        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.error(new RuntimeException("API Error")));
        exchangeRateScheduler.fetchDailyRate();
        verify(exchangeRateApiClient, times(1)).fetchUsdToNtdRates();
        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    void testFetchDailyRate_SaveError() {
        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.just(testRate1));
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));
        exchangeRateScheduler.fetchDailyRate();
        verify(exchangeRateApiClient, times(1)).fetchUsdToNtdRates();
        verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
    }
}
