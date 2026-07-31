package com.vajraiot.VJ_RLY_RFMS_Listener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "device_alarm_status", indexes = {
        @Index(name = "idx_device_id", columnList = "device_id")
})
public class DeviceAlarmStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "is_ignition_on")
    private Boolean isIgnitionON = false;

    @Column(name = "is_vehicle_on")
    private Boolean isVehicleON = false;

    // Vibration Detection
    @Column(name = "is_vibration_detected")
    private Boolean isVibrationDetected = false;

    // Device Communication Alarms
    @Column(name = "is_iot_device_disconnect")
    private Boolean isIotDeviceDisconnect = false;

    @Column(name = "is_vehicle_battery_disconnect")
    private Boolean isVehicleBatteryDisconnect = false;

    // Device Security Alarms
    @Column(name = "is_iot_device_tamper")
    private Boolean isIotDeviceTamper = false;

    // Battery Alarms
    @Column(name = "is_internal_battery_low")
    private Boolean isInternalBatteryLow = false;

    @Column(name = "is_external_battery_low")
    private Boolean isExternalBatteryLow = false;

    // Fuel-Related Alarms
    @Column(name = "is_fuel_theft_alert")
    private Boolean isFuelTheftAlert = false;

    @Column(name = "is_low_fuel_level")
    private Boolean isLowFuelLevel = false;

    @Column(name = "is_fuel_refill_detected")
    private Boolean isFuelRefillDetected = false;

    @Column(name = "packet_timestamp", nullable = false)
    private LocalDateTime packetTimestamp;

    @Column(name = "server_timestamp")
    private LocalDateTime serverTimestamp;

}