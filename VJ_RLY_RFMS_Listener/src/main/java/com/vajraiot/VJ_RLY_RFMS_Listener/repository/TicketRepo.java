package com.vajraiot.VJ_RLY_RFMS_Listener.repository;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.Ticket;
import com.vajraiot.VJ_RLY_RFMS_Listener.enums.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
    Ticket findByDeviceIdAndMessageAndStatus(String deviceId, Events event, String open);
}
