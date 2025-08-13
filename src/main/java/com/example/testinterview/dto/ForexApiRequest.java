package com.example.testinterview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForexApiRequest {
    private String startDate;
    private String endDate;
    private String currency;
}
