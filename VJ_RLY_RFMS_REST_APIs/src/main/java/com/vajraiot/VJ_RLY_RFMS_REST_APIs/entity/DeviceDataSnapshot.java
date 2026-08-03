package com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "device_data_snapshot", indexes = {
        @Index(name = "idx_device_id", columnList = "device_id", unique = true),
        @Index(name = "idx_location", columnList = "latitude,longitude")
})
public class DeviceDataSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true, length = 50)
    private String deviceId;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "heading")
    private Double heading;

    @Column(name = "fleet_state", length = 20)
    private String fleetState; // MOVING, IDLE, STOPPED

    @Column(name = "fuel_level")
    private Double fuelLevel;

    @Column(name = "fuel_level_percentage")
    private Double fuelLevelPercentage;

    @Column(name = "fuel_height")
    private Double fuelHeight;

    @Column(name = "run_hours")
    private Double runHours;

    @Column(name = "internal_battery_voltage")
    private Double internalBatteryVoltage;

    @Column(name = "external_battery_voltage")
    private Double externalBatteryVoltage;

    @Column(name = "is_internal_battery_low")
    private Boolean isInternalBatteryLow = false;

    @Column(name = "is_external_battery_low")
    private Boolean isExternalBatteryLow = false;

    @Column(name = "is_ignition_on")
    private Boolean isIgnitionON = false;

    @Column(name = "is_vehicle_on")
    private Boolean isVehicleON = false;

    @Column(name = "is_vibration_detected")
    private Boolean isVibrationDetected = false;

    @Column(name = "temperature")
    private Double temperature;

    // Active Alerts Count
    @Column(name = "active_alerts_count")
    private Integer activeAlertsCount = 0;

    @Column(name = "packet_timestamp", nullable = false)
    private LocalDateTime packetTimestamp;

    @Column(name = "packet_variant", length = 20)
    private String packetVariant;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
}
