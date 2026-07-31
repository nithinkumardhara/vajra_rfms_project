package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DeviceGPSData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceGPSDataRepo extends JpaRepository<DeviceGPSData, Long> {
}
