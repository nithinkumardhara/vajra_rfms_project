package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceRepo extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);

    List<Device> findByDeviceIdIn(List<String> deviceIds);

    @Query("SELECT DISTINCT d.deviceId FROM DeviceFuelData d")
    List<String> findAllDistinctDeviceIds();
}
