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
@Table(name = "device_fuel_data", indexes = {
        @Index(name = "idx_device_id", columnList = "device_id"),
        @Index(name = "idx_fuel_theft_alert", columnList = "device_id,is_fuel_theft_alert"),
        @Index(name = "idx_fuel_level", columnList = "device_id,fuel_level")
})
public class DeviceFuelData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

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

    @Column(name = "in_flow")
    private Double inFlow;

    @Column(name = "out_flow")
    private Double outFlow;

    @Column(name = "net_flow")
    private Double netFlow;

    @Column(name = "run_hours")
    private Double runHours;

    @Column(name = "sensor_fms_alarms")
    private Integer sensorFmsAlarms;

    @CreationTimestamp
    @Column(name = "server_timestamp", nullable = false)
    private LocalDateTime serverTimestamp;

    @Column(name = "packet_timestamp", nullable = false)
    private LocalDateTime packetTimestamp;


}
