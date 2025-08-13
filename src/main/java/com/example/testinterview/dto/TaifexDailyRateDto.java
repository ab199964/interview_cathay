package com.example.testinterview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaifexDailyRateDto {

    @JsonProperty("Date")
    private String date;

    @JsonProperty("USD/NTD")
    private String usdToNtd;
}
