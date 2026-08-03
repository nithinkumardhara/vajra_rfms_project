package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FleetTripTimelineDTO {

    private String deviceId;
    private LocalDate reportDate;
    private Double totalDistance;
    private Integer tripCount;
    private List<TripDTO> trips;

}
