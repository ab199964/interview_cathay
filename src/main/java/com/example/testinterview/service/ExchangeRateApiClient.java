package com.example.testinterview.service;


import com.example.testinterview.entity.ExchangeRate;
import reactor.core.publisher.Flux;

public interface ExchangeRateApiClient {
    Flux<ExchangeRate> fetchUsdToNtdRates();
}