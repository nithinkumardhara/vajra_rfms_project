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
public class FuelDataDTO {

    private String deviceId;
    private Double fuelLevel;  // latest fuel level
    private Double tankCapacity;
    private Double fuelConsumption;
    private Double fuelFill; // latest state like moving, idle, stopped
    private Double fuelTheft;
    private String fleetSate;
}
