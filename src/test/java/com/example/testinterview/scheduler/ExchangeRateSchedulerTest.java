package com.example.testinterview.scheduler;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.entity.ExchangeRate;
import com.example.testinterview.mapper.ExchangeRateMapper;
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
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateSchedulerTest {

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateMapper exchangeRateMapper;

    @InjectMocks
    private ExchangeRateScheduler exchangeRateScheduler;

    private ExchangeRateDto sampleDto;
    private ExchangeRate sampleEntity;

    @BeforeEach
    void setUp() {
        // DTO 原始資料（String）
        sampleDto = new ExchangeRateDto();
        sampleDto.setDate("20250814");
        sampleDto.setUsdToNtd("31.25");
        // Entity 對應資料
        sampleEntity = new ExchangeRate();
        sampleEntity.setDate(LocalDateTime.of(2025, 8, 14, 0, 0));
        sampleEntity.setUsdToNtd(new BigDecimal("31.25"));
    }

    @Test
    void fetchDailyRate_ShouldSaveExchangeRate_WhenApiReturnsData() {
        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.just(sampleDto));
        when(exchangeRateMapper.toEntity(any(ExchangeRateDto.class))).thenReturn(sampleEntity);
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(invocation -> {
            ExchangeRate entity = invocation.getArgument(0);
            return Mono.just(entity);
        });

        exchangeRateScheduler.fetchDailyRate();

        verify(exchangeRateApiClient, times(1)).fetchUsdToNtdRates();
        verify(exchangeRateMapper, times(1)).toEntity(any(ExchangeRateDto.class));
        verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
    }

    @Test
    void fetchDailyRate_ShouldHandleError_WhenApiClientFails() {
        RuntimeException expectedException = new RuntimeException("API调用失败");
        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.error(expectedException));

        exchangeRateScheduler.fetchDailyRate();

        verify(exchangeRateApiClient, times(1)).fetchUsdToNtdRates();
        verify(exchangeRateMapper, never()).toEntity(any(ExchangeRateDto.class));
        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    void fetchDailyRate_ShouldSetCurrentUTCTime_WhenMappingEntity() {
        when(exchangeRateApiClient.fetchUsdToNtdRates()).thenReturn(Flux.just(sampleDto));
        when(exchangeRateMapper.toEntity(any(ExchangeRateDto.class))).thenReturn(sampleEntity);
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(invocation -> {
            ExchangeRate entity = invocation.getArgument(0);
            assert entity.getCreatedAt() != null;
            return Mono.just(entity);
        });

        exchangeRateScheduler.fetchDailyRate();

        verify(exchangeRateRepository).save(argThat(rate ->
                rate.getCreatedAt() != null &&
                        rate.getCreatedAt().atOffset(ZoneOffset.UTC) != null
        ));
    }
}
