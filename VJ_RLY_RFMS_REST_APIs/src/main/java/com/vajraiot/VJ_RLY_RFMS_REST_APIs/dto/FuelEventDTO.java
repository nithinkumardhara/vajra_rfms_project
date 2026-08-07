package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FuelEventDTO {

    private String deviceId;
    private String eventType;   // Fill, Theft, Low
    private LocalTime eventTime;
    private Double fuelVolume;
    private Double latitude;
    private Double longitude;
    private Double startFuelLevel;
    private Double endFuelLevel;
//    private Double eventDuration;
}
