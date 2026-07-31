package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceFuelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceFuelDataRepo extends JpaRepository<DeviceFuelData, Long> {

    List<DeviceFuelData> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(String deviceId, LocalDateTime startTime, LocalDateTime endTime);

    List<DeviceFuelData> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(String deviceId, LocalDateTime start, LocalDateTime end);

    @Query("""
           SELECT d
           FROM DeviceFuelData d
           WHERE d.packetTimestamp >= :start
           ORDER BY d.deviceId,d.packetTimestamp ASC
           """)
    List<DeviceFuelData> findLast24HoursData(LocalDateTime start);

    @Query("""
           SELECT d
           FROM DeviceFuelData d
           WHERE d.deviceId = :deviceId
           AND d.packetTimestamp >= :start
           ORDER BY d.packetTimestamp DESC
           """)
    List<DeviceFuelData> findLast24HoursDataByDeviceId(String deviceId, LocalDateTime start);

    @Query("""
            SELECT d
            FROM DeviceFuelData d
            WHERE d.deviceId IN (:allDeviceIds)
            ORDER BY d.deviceId, d.packetTimestamp DESC
            """)
    List<DeviceFuelData> findLatestPacketForDevices(List<String> allDeviceIds);

    @Query("""
           SELECT f
           FROM DeviceFuelData f
           WHERE f.deviceId = :deviceId
           ORDER BY f.packetTimestamp DESC
           LIMIT 1
           """)
    DeviceFuelData findLatestPacketByDeviceId(String deviceId);
}
