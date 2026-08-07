package com.vajraiot.VJ_RLY_RFMS_REST_APIs.util;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FuelEventDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFuelMetrics {
    private String deviceId;
    private double fuelConsumption;
    private double fuelFill;
    private double fuelTheft;
    private double distanceKm;
    private double runHours;
    private double efficiency;
    private double latestFuelLevel;
    private String latestFleetState;
    private List<FuelEventDTO> events;

    public static DeviceFuelMetrics empty() {
        return DeviceFuelMetrics.builder()
                .fuelConsumption(0).fuelFill(0).fuelTheft(0)
                .distanceKm(0).runHours(0).efficiency(0)
                .events(Collections.emptyList())
                .build();
    }
}