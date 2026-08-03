package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FleetRunHoursDTO {

    private String deviceId;
    private LocalTime movingRunHours;
    private LocalTime stoppedRunHours;
    private LocalTime idleRunHours;
    private Double totalDistance;
    private String period; // Range
}