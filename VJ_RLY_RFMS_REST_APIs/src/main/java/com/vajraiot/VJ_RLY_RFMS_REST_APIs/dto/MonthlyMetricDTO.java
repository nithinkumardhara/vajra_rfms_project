package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyMetricDTO {
    private String deviceId;
    private Double distance;
    private Double fuelConsumed;
    private Double fuelFill;
}