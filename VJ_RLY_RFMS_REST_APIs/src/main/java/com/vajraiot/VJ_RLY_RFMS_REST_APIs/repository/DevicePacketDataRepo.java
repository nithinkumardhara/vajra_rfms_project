package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DevicePacketData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DevicePacketDataRepo extends JpaRepository<DevicePacketData, Long>{
    
    List<DevicePacketData> findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(String deviceId, LocalDateTime from, LocalDateTime to);

    List<DevicePacketData> findByDeviceIdInAndPacketTimestampBetweenOrderByDeviceIdAscPacketTimestampAsc(List<String> deviceIds, LocalDateTime from, LocalDateTime to);
}
