package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FuelReportDTO {

    private String reportType;          // DAILY, WEEKLY, MONTHLY, CUSTOM
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private String deviceId;
    private Double fuelTankCapacity;

    private Double openingFuelLevel;
    private Double closingFuelLevel;
    private Double totalConsumption;
    private Double runningHours;

    // Refill
    private Integer refillCount;
    private Double totalRefillQuantity;
    private List<RefillEventDTO> refillEvents;
    // Theft
    private Integer theftCount;
    private Double theftQuantity;
    private List<TheftEventDTO> theftEvents;

    private Double distanceTraveled;

    private List<FuelRecordDTO> fuelReports;
}
