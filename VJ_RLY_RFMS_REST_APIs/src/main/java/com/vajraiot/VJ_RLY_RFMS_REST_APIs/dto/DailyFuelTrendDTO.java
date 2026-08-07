package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DailyFuelTrendDTO {

    private String date;                 // "2026-08-01" for All device summary
    private Double totalFuelConsumption;
    private Double totalFuelFilled;
    private Double totalFuelTheft;
    private Double avgDailyFuelEfficiency;
}
