package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiClientImplTest {

    @InjectMocks
    private ExchangeRateApiClientImpl exchangeRateApiClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        // 設定測試用 URL 與 URI
        ReflectionTestUtils.setField(exchangeRateApiClient, "baseUrl", mockWebServer.url("/").toString());
        ReflectionTestUtils.setField(exchangeRateApiClient, "exchangeRatesUri", "/api/exchange-rates");
        // 手動初始化 WebClient
        exchangeRateApiClient.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchUsdToNtdRates_ShouldReturnValidRates() {
        String mockResponseJson = """
                [
                    {"Date": "20250814", "USD/NTD": "31.25"},
                    {"Date": "20250813", "USD/NTD": "31.15"},
                    {"Date": "20250812", "USD/NTD": null},
                    {"Date": "20250811", "USD/NTD": "30.95"}
                ]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        Flux<ExchangeRateDto> result = exchangeRateApiClient.fetchUsdToNtdRates();

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getDate().equals("20250814") && dto.getUsdToNtd().equals("31.25"))
                .expectNextMatches(dto -> dto.getDate().equals("20250813") && dto.getUsdToNtd().equals("31.15"))
                .expectNextMatches(dto -> dto.getDate().equals("20250811") && dto.getUsdToNtd().equals("30.95"))
                .expectComplete()
                .verify();
    }

    @Test
    void fetchUsdToNtdRates_ShouldReturnValidData() {
        String mockResponseJson = """
                [
                    {"Date": "20250812", "USD/NTD": "31.05"},
                    {"Date": "20250813", "USD/NTD": "31.15"}
                ]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        Flux<ExchangeRateDto> result = exchangeRateApiClient.fetchUsdToNtdRates();

        StepVerifier.create(result)
                .expectNextMatches(dto -> "20250812".equals(dto.getDate()) && "31.05".equals(dto.getUsdToNtd()))
                .expectNextMatches(dto -> "20250813".equals(dto.getDate()) && "31.15".equals(dto.getUsdToNtd()))
                .expectComplete()
                .verify();
    }

}