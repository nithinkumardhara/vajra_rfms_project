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
public class VehicleFuelSummaryDTO {

    private String deviceId; // each vehicle fuel summary for week and month
    private Double fuelConsumption;
    private Double fuelFill;
    private Double fuelTheft;
    private Double avgMileage;
}
