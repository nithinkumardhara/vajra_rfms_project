package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TripDTO {

    private Integer tripId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime durationHours;
    private Double distance;
    private Double avgSpeed;
    private Double starLatitude;
    private Double starLongitude;
    private Double endLatitude;
    private Double endLongitude;
}
