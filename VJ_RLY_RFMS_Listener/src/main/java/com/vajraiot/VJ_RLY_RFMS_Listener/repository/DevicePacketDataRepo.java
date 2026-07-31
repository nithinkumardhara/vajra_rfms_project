package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DevicePacketData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevicePacketDataRepo extends JpaRepository<DevicePacketData, Long>{
}
