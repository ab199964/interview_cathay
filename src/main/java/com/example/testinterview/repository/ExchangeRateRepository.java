package com.example.testinterview.repository;


import com.example.testinterview.entity.ExchangeRate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExchangeRateRepository extends ReactiveMongoRepository<ExchangeRate, String> {
    Flux<ExchangeRate> findByDateBetween(String startDate, String endDate);

}