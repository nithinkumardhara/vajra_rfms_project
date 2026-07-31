package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.TicketDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Ticket;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.TicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepo ticketRepository;

    public Page<TicketDTO> getTickets(String deviceId, LocalDate startDate, LocalDate endDate, int pageNum, int pageSize) {
        Pageable pageable =  PageRequest.of(pageNum, pageSize, Sort.by("raiseTimestamp").descending());
        if(deviceId == null || startDate == null || endDate == null) {
            Page<Ticket> tickets = ticketRepository.findLatestTicketForEachDevice(pageable);
            return tickets.map(this::mapToDto);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        Page<Ticket> tickets = ticketRepository.findByDeviceIdAndRaiseTimestampBetweenOrderByRaiseTimestampDesc(deviceId, startDateTime, endDateTime, pageable );
        return tickets.map(this::mapToDto);
    }

    private TicketDTO mapToDto(Ticket ticket){
        return TicketDTO.builder()
                .status(ticket.getStatus())
                .message(ticket.getMessage())
                .raiseTime(ticket.getRaiseTimestamp())
                .closeTime(ticket.getCloseTimestamp())
                .deviceId(ticket.getDeviceId())
                .serverTime(ticket.getServerTimestamp())
                .build();
    }

    // Export tickets
    public List<TicketDTO> getTicketsForExport(String deviceId, LocalDate startDate, LocalDate endDate) {
        if(deviceId == null || startDate == null || endDate == null){

            return ticketRepository.findLatestTicketForEachDevice(Pageable.unpaged())
                    .stream()
                    .map(this::mapToDto)
                    .toList();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        return ticketRepository.findByDeviceIdAndRaiseTimestampBetweenOrderByRaiseTimestampDesc(
                        deviceId,
                        startDateTime,
                        endDateTime,
                        Pageable.unpaged()
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }
}
