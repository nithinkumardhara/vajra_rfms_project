package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DeviceAlarmStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceAlarmStatusRepo extends JpaRepository<DeviceAlarmStatus, Long> {
}
