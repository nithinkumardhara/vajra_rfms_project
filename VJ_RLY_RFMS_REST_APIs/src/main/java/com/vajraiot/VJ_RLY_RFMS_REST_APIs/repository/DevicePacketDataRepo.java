package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DevicePacketData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevicePacketDataRepo extends JpaRepository<DevicePacketData, Long>{
}
