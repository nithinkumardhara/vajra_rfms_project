package com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "device_gps_data", indexes = {
        @Index(name = "idx_device_timestamp", columnList = "device_id,packet_timestamp"),
        @Index(name = "idx_location", columnList = "latitude,longitude"),
        @Index(name = "idx_movement_status", columnList = "device_id,movement_status")
})
public class DeviceGPSData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "gps_fix", length = 10)
    private Integer gpsFix;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude_direction", length = 1)
    private String latitudeDirection;

    @Column(name = "longitude_direction", length = 1)
    private String longitudeDirection;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "heading")
    private Double heading;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "no_of_satellites")
    private Integer noOfSatellites;

    @Column(name = "pdop") // Position Dilution of Precision
    private Double pdop;

    @Column(name = "hdop") // Horizontal Dilution of Precision
    private Double hdop;

    @Column(name = "fleet_state", length = 20)
    private String fleetState; // MOVING, IDLE, STOPPED

    @Column(name = "server_timestamp", nullable = false)
    private LocalDateTime serverTimestamp;

    @Column(name = "gps_date", length = 10)
    private String gpsDate;

    @Column(name = "gps_time", length = 10)
    private String gpsTime;

    @Column(name = "packet_timestamp", nullable = false)
    private LocalDateTime packetTimestamp;


}
