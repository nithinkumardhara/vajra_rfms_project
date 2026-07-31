package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DeviceDataSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceDataSnapshotRepo extends JpaRepository<DeviceDataSnapshot, Long> {
    Optional<DeviceDataSnapshot> findByDeviceId(String deviceId);
}
