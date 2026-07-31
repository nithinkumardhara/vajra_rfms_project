package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceGPSDataRepo extends JpaRepository<DeviceGPSData, Long> {
    Optional<DeviceGPSData> findFirstByDeviceIdOrderByPacketTimestampDesc(String deviceId);

    List<DeviceGPSData> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(String deviceId, LocalDateTime startTime, LocalDateTime endTime);

    List<DeviceGPSData> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(String deviceId, LocalDateTime start, LocalDateTime end);
}
