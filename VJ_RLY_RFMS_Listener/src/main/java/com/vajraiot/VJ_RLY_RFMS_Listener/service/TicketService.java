package com.vajraiot.VJ_RLY_RFMS_Listener.service;

import com.vajraiot.VJ_RLY_RFMS_Listener.entity.DeviceAlarmStatus;
import com.vajraiot.VJ_RLY_RFMS_Listener.entity.Ticket;
import com.vajraiot.VJ_RLY_RFMS_Listener.enums.Events;
import com.vajraiot.VJ_RLY_RFMS_Listener.repository.TicketRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepo ticketRepository;

    @Transactional
    public void processTickets(String deviceId, DeviceAlarmStatus alarmStatus) {

        for (Events event : Events.values()) {
            boolean active = isEventActive(event, alarmStatus);

            Ticket openTicket = ticketRepository.findByDeviceIdAndMessageAndStatus(deviceId, event, "OPEN");

            if (active) {
                if (openTicket == null) {
                    openTicket(deviceId, event, alarmStatus.getPacketTimestamp());
                }
            } else {
                if (openTicket != null) {
                    closeTicket(openTicket, alarmStatus.getPacketTimestamp(), event);
                }
            }
        }
    }

    private boolean isEventActive(Events event, DeviceAlarmStatus alarmStatus) {
        return switch (event) {
            case DEVICE_DISCONNECT -> alarmStatus.getIsIotDeviceDisconnect();
            case VEHICLE_BATTERY_DISCONNECT -> alarmStatus.getIsVehicleBatteryDisconnect();
            case DEVICE_TAMPER -> alarmStatus.getIsIotDeviceTamper();
            case INTERNAL_BATTERY_LOW -> alarmStatus.getIsInternalBatteryLow();
            case EXTERNAL_BATTERY_LOW -> alarmStatus.getIsExternalBatteryLow();
            case FUEL_THEFT_ACTIVE -> alarmStatus.getIsFuelTheftAlert();
            case FUEL_FILL_ACTIVE -> alarmStatus.getIsFuelRefillDetected();
        };
    }

    private void openTicket(String deviceId, Events event, LocalDateTime packetTime) {

        Ticket ticket = Ticket.builder()
                .deviceId(deviceId)
                .message(event)
                .status("OPEN")
                .raiseTimestamp(packetTime)
                .serverTimestamp(LocalDateTime.now())
                .build();

        ticketRepository.save(ticket);
        log.info("Ticket {} opened [{}]", ticket.getId(), event);
    }

    private void closeTicket(Ticket ticket, LocalDateTime closeTime, Events event) {

        ticket.setCloseTimestamp(closeTime);
        ticket.setStatus("CLOSED");

        ticketRepository.save(ticket);
        log.info("Ticket {} closed [{}]", ticket.getId(), event);
    }
}