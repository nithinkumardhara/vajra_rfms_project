package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DeviceFuelData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceFuelDataRepo extends JpaRepository<DeviceFuelData, Long> {
}
