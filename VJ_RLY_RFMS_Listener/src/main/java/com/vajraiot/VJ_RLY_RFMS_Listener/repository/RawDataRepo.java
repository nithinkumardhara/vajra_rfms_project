package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepo extends JpaRepository<RawData, Long> {
}
