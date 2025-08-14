package com.example.testinterview.service.impl;

import com.example.testinterview.dto.ExchangeRateDto;
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
        Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(
                new ObjectMapper(),
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM
        );

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(decoder))
                .build();

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .build();
    }

    @Override
    public Flux<ExchangeRateDto> fetchUsdToNtdRates() {
        return webClient.get()
                .uri(exchangeRatesUri)
                .retrieve()
                .bodyToFlux(ExchangeRateDto.class)
                .filter(dto -> dto != null && dto.hasValidUsdToNtd());
    }
}
