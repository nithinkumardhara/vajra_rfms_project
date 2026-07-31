package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    @Query("""
        SELECT t FROM Ticket t
        WHERE t.raiseTimestamp = (
            SELECT MAX(t2.raiseTimestamp)
            FROM Ticket t2
            WHERE t2.deviceId = t.deviceId
        )
    """)
    Page<Ticket> findLatestTicketForEachDevice(Pageable pageable);

    Page<Ticket> findByDeviceIdAndRaiseTimestampBetweenOrderByRaiseTimestampDesc(String deviceId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
