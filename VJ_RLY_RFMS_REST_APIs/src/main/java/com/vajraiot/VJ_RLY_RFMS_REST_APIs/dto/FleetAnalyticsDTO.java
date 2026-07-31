package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FleetAnalyticsDTO {

    private String deviceId;
    private Double currentLatitude;
    private Double currentLongitude;
    private String movementStatus;      // MOVING, IDLE, STOPPED
    private Double totalDistanceTraveled;
    private Integer totalMoving;
    private Integer totalStopped;
    private Integer totalIdles;
    private String totalMovingDuration;
    private String totalStoppedDuration;
    private String totalIdleDuration;
    private Double averageSpeed;

    private List<GPSRecordDTO> dataPointRecords;
}
