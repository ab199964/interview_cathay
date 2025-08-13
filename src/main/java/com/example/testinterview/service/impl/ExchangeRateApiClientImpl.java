package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.entity.ExchangeRate;
import com.example.testinterview.service.ExchangeRateApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExchangeRateApiClientImpl implements ExchangeRateApiClient {

    @Value("${taifex.base-url}")
    private String baseUrl;

    @Value("${taifex.exchange-rates-uri}")
    private String exchangeRatesUri;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        // 使用 Jackson2JsonDecoder 來處理 JSON 解碼 打API拿到非正常的JSON格式
        Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(
                new ObjectMapper(),
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM
        );

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(decoder))
                .build();
        this.webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }


    @Override
    public Flux<ExchangeRate> fetchUsdToNtdRates() {
        return webClient.get()

                .uri(baseUrl + exchangeRatesUri)
                .retrieve()
                .bodyToFlux(ExchangeRateDto.class)
                .filter(dto -> dto.getUsdToNtd() != null && !dto.getUsdToNtd().isEmpty())
                .map(dto -> ExchangeRate.builder()
                        .date(dto.getDate())
                        .usdToNtd(new BigDecimal(dto.getUsdToNtd()))
                        .createdAt(LocalDateTime.now())
                        .build());
    }
}