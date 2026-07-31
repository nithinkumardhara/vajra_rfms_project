package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DeviceGeneralData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceGeneralDataRepo extends JpaRepository<DeviceGeneralData,Long> {
}
