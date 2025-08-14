package com.example.testinterview.controller;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.response.ApiErrorException;
import com.example.testinterview.response.ApiResponse;
import com.example.testinterview.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @PostMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<ExchangeRateDto>> forex(@RequestBody ExchangeRateDto request) {
        log.info("接收到匯率歷史查詢請求: {}", request);
        return exchangeRateService.getExchangeRateHistory(request)
                .map(ApiResponse::success)
                .onErrorResume(ApiErrorException.class, ex ->
                        Mono.just(ApiResponse.error(ex.getCode(), ex.getMessage()))
                );

    }
}
