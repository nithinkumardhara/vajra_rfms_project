package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepo extends JpaRepository<RawData, Long> {
}
