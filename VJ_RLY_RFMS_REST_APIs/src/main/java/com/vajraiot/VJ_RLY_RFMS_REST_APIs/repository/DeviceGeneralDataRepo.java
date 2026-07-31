package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGeneralData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceGeneralDataRepo extends JpaRepository<DeviceGeneralData,Long> {
    Optional<DeviceGeneralData> findFirstByDeviceIdOrderByPacketTimestampDesc(String deviceId);
}
