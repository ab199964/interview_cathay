package com.example.testinterview.scheduler;

import com.example.testinterview.mapper.ExchangeRateMapper;
import com.example.testinterview.repository.ExchangeRateRepository;
import com.example.testinterview.service.ExchangeRateApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateScheduler {


    private final ExchangeRateApiClient exchangeRateApiClient;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;

    @Scheduled(cron = "${schedulers.exchange-rate.cron}", zone = "Asia/Taipei")
    public void fetchDailyRate() {
        log.info("Triggering daily exchange rate fetch job...");
        exchangeRateApiClient.fetchUsdToNtdRates()
                .map(exchangeRateMapper::toEntity)
                .map(exchangeRate -> {
                    exchangeRate.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    return exchangeRate;})
                .flatMap(exchangeRateRepository::save)
                .doOnNext(rate -> log.info("Successfully saved rate: {}", rate))
                .doOnError(error -> log.error("Error during daily rate fetch job", error))
                .subscribe();
    }
}
