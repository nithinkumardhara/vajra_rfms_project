package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FuelPerformanceDTO {

    private Double totalFuelConsumption;
    private Double totalFuelFill;
    private Double totalFuelTheft;
    private Double avgFuelEfficiency;
    private String range;   // selected calendar dates
    private List<DailyFuelTrendDTO>  dailyFuelTrend;  // all vehicles daily fuel summary trend for week and month
    private List<VehicleFuelSummaryDTO>  vehicleFuelSummary;  // each vehicle fuel trend for week and month
    private Double totalFuelInFleets; // today | day wise
    private List<FuelEventDTO> eventList; // each device latest fuel events only for today and daywise
    private List<FuelDataDTO> fuelData; // each vehicle day fuel trend for today and daywise

}
