package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceAlarmStatusRepo extends JpaRepository<DeviceAlarmStatus, Long> {
    Optional<DeviceAlarmStatus> findFirstByDeviceIdOrderByPacketTimestampDesc(String deviceId);

    List<DeviceAlarmStatus> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(String deviceId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
        SELECT d FROM DeviceAlarmStatus d
        WHERE d.packetTimestamp = (
            SELECT MAX(ds.packetTimestamp)
            FROM DeviceAlarmStatus ds
            WHERE ds.deviceId = d.deviceId
        )
    """)
    List<DeviceAlarmStatus> findLatestAlarmStatusForAllDevices();

    List<DeviceAlarmStatus> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(String deviceId, LocalDateTime start, LocalDateTime end);
}