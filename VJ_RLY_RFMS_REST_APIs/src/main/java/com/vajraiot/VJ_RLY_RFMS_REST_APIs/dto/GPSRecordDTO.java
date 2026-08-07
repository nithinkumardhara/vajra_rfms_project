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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GPSRecordDTO {
    private Long id;
    private String deviceId;
    private Integer gpsFix;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private Double altitude;
    private Integer noOfSatellites;
    private Double pdop;
    private Double hdop;
    private String movementStatus;
    private LocalDateTime packetTimestamp;
}
