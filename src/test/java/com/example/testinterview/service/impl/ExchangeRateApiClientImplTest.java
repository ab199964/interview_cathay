package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.entity.ExchangeRate;
import com.example.testinterview.service.ExchangeRateApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiClientImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ExchangeRateApiClientImpl exchangeRateApiClient;

    @BeforeEach
    void setUp() {
        exchangeRateApiClient = new ExchangeRateApiClientImpl();
        ReflectionTestUtils.setField(exchangeRateApiClient, "baseUrl", "https://openapi.taifex.com.tw");
        ReflectionTestUtils.setField(exchangeRateApiClient, "exchangeRatesUri", "/v1/DailyForeignExchangeRates");
        ReflectionTestUtils.setField(exchangeRateApiClient, "webClient", webClient);
    }

    @Test
    void testFetchUsdToNtdRates_Success() {
        // 准备测试数据
        ExchangeRateDto dto1 = new ExchangeRateDto();
        dto1.setDate("20250725");
        dto1.setUsdToNtd("29.46");

        ExchangeRateDto dto2 = new ExchangeRateDto();
        dto2.setDate("20250726");
        dto2.setUsdToNtd("29.50");

        ExchangeRateDto dto3 = new ExchangeRateDto(); // 没有汇率的数据，应被过滤
        dto3.setDate("20250727");

        // 模拟 WebClient 行为
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ExchangeRateDto.class)).thenReturn(Flux.fromIterable(Arrays.asList(dto1, dto2, dto3)));

        // 执行被测方法
        Flux<ExchangeRate> result = exchangeRateApiClient.fetchUsdToNtdRates();

        // 验证结果
        StepVerifier.create(result)
            .expectNextMatches(rate ->
                rate.getDate().equals("20250725") &&
                rate.getUsdToNtd().compareTo(new BigDecimal("29.46")) == 0 &&
                rate.getCreatedAt() != null
            )
            .expectNextMatches(rate ->
                rate.getDate().equals("20250726") &&
                rate.getUsdToNtd().compareTo(new BigDecimal("29.50")) == 0 &&
                rate.getCreatedAt() != null
            )
            .expectComplete()
            .verify();
    }

    @Test
    void testFetchUsdToNtdRates_EmptyResponse() {
        // 模拟 WebClient 返回空结果
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ExchangeRateDto.class)).thenReturn(Flux.empty());

        // 执行被测方法
        Flux<ExchangeRate> result = exchangeRateApiClient.fetchUsdToNtdRates();

        // 验证结果
        StepVerifier.create(result)
            .expectComplete()
            .verify();
    }

    @Test
    void testFetchUsdToNtdRates_ErrorHandling() {
        // 模拟 WebClient 抛出异常
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ExchangeRateDto.class)).thenReturn(Flux.error(new RuntimeException("API Error")));

        // 执行被测方法
        Flux<ExchangeRate> result = exchangeRateApiClient.fetchUsdToNtdRates();

        // 验证结果
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }
}
