package com.vajraiot.VJ_RLY_RFMS_Listener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "devices", indexes = {
        @Index(name = "idx_device_id", columnList = "device_id", unique = true),
        @Index(name = "idx_imei", columnList = "imei", unique = true)
})
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true, length = 50)
    private String deviceId;

    @Column(name = "imei", nullable = false, unique = true, length = 20)
    private String imei;

    @Column(name = "sim_number", length = 20)
    private String simNumber;

    @Column(name = "version", length = 10)
    private String version;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "manufacturer_id", length = 50)
    private Integer manufacturerId;

    @Column(name = "fuel_tank_capacity", length = 10)
    private String fuelTankCapacity; // e.g., "1200L"

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "last_communication_time")
    private LocalDateTime lastCommunicationTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
