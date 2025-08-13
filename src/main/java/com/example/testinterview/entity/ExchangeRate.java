package com.example.testinterview.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "exchange_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
    @Id
    private String id;
    private String date; // yyyyMMdd
    private BigDecimal usdToNtd;
    private LocalDateTime createdAt;
}