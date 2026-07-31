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
@Table(name = "device_packet_data", indexes = {
        @Index(name = "idx_device_timestamp", columnList = "device_id,packet_timestamp"),
        @Index(name = "idx_raw_data_id", columnList = "raw_data_id")
})
public class DevicePacketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_data_id")
    private Long rawDataId; // Foreign key reference to RawData

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "packet_variant", length = 20)
    private String packetVariant;

    @Column(name = "imei", length = 20)
    private String imei;

    @Column(name = "sim_number", length = 20)
    private String simNumber;

    @Column(name = "version", length = 10)
    private String version;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "internal_battery_voltage")
    private Double internalBatteryVoltage;

    @Column(name = "external_battery_voltage")
    private Double externalBatteryVoltage;

    @Column(name = "consumption")
    private Double consumption;

    @Column(name = "temperature")
    private Double temperature;

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

    @Column(name = "no_of_satellites")
    private Integer noOfSatellites;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "pdop")
    private Double pdop;

    @Column(name = "hdop")
    private Double hdop;

    @Column(name = "manufacturer_id")
    private Integer manufacturerId;

    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    @Column(name = "fuel_level_height")
    private Double fuelLevelHeight;

    @Column(name = "fuel_level")
    private Double fuelLevel;

    @Column(name = "fuel_level_percentage")
    private Double fuelLevelPercentage;

    @Column(name = "sensor_fms_alarms")
    private Integer sensorFmsAlarms;

    @Column(name = "vehicle_battery_voltage")
    private Double vehicleBatteryVoltage;

    @Column(name = "run_hours")
    private Double runHours;

    @Column(name = "in_flow")
    private Double inFlow;

    @Column(name = "out_flow")
    private Double outFlow;

    @Column(name = "is_ignition_on")
    private Boolean isIgnitionON = false;

    @Column(name = "is_iot_device_disconnect")
    private Boolean isIotDeviceDisconnect = false;

    @Column(name = "is_vehicle_battery_disconnect")
    private Boolean isVehicleBatteryDisconnect = false;

    @Column(name = "is_iot_device_tamper")
    private Boolean isIotDeviceTamper = false;

    @Column(name = "is_internal_battery_low")
    private Boolean isInternalBatteryLow = false;

    @Column(name = "is_external_battery_low")
    private Boolean isExternalBatteryLow = false;

    @Column(name = "is_vibration_status")
    private Boolean isVibrationStatus = false;

    @Column(name = "is_vehicle_on")
    private Boolean isVehicleON = false;

    @Column(name = "is_low_fuel_level")
    private Boolean isLowFuelLevel = false;

    @Column(name = "is_fuel_theft")
    private Boolean isFuelTheft = false;

    @Column(name = "is_fuel_refill")
    private Boolean isFuelRefill = false;

    @Column(name = "is_low_battery")
    private Boolean isLowBattery = false;

    @Column(name = "packet_timestamp", nullable = false)
    private LocalDateTime packetTimestamp;

    @Column(name = "server_timestamp", nullable = false)
    private LocalDateTime serverTimestamp;

}
