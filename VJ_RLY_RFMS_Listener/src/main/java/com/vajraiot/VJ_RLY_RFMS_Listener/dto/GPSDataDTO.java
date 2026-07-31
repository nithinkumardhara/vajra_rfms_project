package com.vajraiot.VJ_RLY_RFMS_Listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSDataDTO {
    private Integer gpsFix;
    private String date;
    private String time;
    private Double latitude;
    private String latitudeDirection;
    private Double longitude;
    private String longitudeDirection;
    private Double speed;
    private Double heading;
    private Integer noOfSatellites;
    private Double altitude;
    private Double pdop;
    private Double hdop;
}
