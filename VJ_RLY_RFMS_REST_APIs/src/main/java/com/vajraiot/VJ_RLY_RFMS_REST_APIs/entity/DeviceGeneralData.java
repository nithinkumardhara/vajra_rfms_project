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
@Table(name = "device_general_data", indexes = {
        @Index(name = "idx_device_id", columnList = "device_id")
})
public class DeviceGeneralData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "internal_battery_voltage")
    private Double internalBatteryVoltage;

    @Column(name = "external_battery_voltage")
    private Double externalBatteryVoltage;

    @Column(name = "is_internal_battery_low")
    private Boolean isInternalBatteryLow = false;

    @Column(name = "is_external_battery_low")
    private Boolean isExternalBatteryLow = false;

    @Column(name = "consumption")
    private Double consumption;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "server_timestamp", nullable = false)
    private LocalDateTime serverTimestamp;

    @Column(name = "packet_timestamp", nullable = false)
    private LocalDateTime packetTimestamp;

}
