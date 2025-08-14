package com.example.testinterview.repository;


import com.example.testinterview.entity.ExchangeRate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface ExchangeRateRepository extends ReactiveMongoRepository<ExchangeRate, String> {

    @Query("{ 'date': { $gte: ?0, $lte: ?1 }, 'usdToNtd': { $exists: true } }")
    Flux<ExchangeRate> findUsdBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

}


