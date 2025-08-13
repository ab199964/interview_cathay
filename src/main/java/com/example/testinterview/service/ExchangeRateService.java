package com.example.testinterview.service;

import com.example.testinterview.dto.ExchangeRateApiRequest;
import com.example.testinterview.dto.ExchangeRateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExchangeRateService {
    Mono<List<ExchangeRateDto>> getExchangeRateHistory(ExchangeRateApiRequest exchangeRateRequest);
}