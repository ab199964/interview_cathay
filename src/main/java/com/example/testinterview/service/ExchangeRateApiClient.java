package com.example.testinterview.service;


import com.example.testinterview.dto.ExchangeRateDto;
import reactor.core.publisher.Flux;

public interface ExchangeRateApiClient {
    Flux<ExchangeRateDto> fetchUsdToNtdRates();
}