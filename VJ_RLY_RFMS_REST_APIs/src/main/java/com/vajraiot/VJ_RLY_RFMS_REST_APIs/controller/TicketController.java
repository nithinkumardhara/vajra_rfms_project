package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.TicketDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
@Slf4j
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/get/all/tickets")
    public ResponseEntity<Page<TicketDTO>> getTickets(@RequestParam(required = false) String deviceId,
                                                      @RequestParam(required = false) LocalDate startDate,
                                                      @RequestParam(required = false) LocalDate endDate,
                                                      @RequestParam(required = false, defaultValue = "0") int pageNum,
                                                      @RequestParam(required = false, defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(ticketService.getTickets(deviceId, startDate, endDate, pageNum, pageSize));
    }

}
