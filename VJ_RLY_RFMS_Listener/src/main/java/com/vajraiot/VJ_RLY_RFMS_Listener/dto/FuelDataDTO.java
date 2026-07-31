package com.vajraiot.VJ_RLY_RFMS_Listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelDataDTO {
    private Integer manufacturerId;
    private String serialNumber;
    private Double fuelLevelHeight;
    private Double fuelLevel;
    private Double fuelLevelPercentage;
    private Integer sensorFMSAlarms;
    private Double vehicleBatteryVoltage;
    private Double runHours;
    private Double inFlow;
    private Double outFlow;
}
