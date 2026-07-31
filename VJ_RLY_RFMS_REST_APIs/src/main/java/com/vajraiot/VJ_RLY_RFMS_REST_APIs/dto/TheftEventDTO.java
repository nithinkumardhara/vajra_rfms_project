package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TheftEventDTO {

    private LocalDateTime timestamp;
    private Double theftQuantity;
    private Double fuelLevelBefore;
    private Double fuelLevelAfter;
    private Double fuelConsumption;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double durationMinutes;
    private Boolean IgnitionON;
}
