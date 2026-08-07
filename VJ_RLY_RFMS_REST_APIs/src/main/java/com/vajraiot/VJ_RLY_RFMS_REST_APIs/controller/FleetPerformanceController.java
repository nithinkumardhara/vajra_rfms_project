package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FleetPerformanceDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FleetRunHoursDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FleetTripTimelineDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FleetPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/fleet")
@RequiredArgsConstructor
public class FleetPerformanceController {

    private final FleetPerformanceService fleetPerformanceService;

    @GetMapping("/run-hours")
    public ResponseEntity<List<FleetRunHoursDTO>> getFleetRunHours(@RequestParam(required = false) String deviceId,
                                                                   @RequestParam String filter,
                                                                   @RequestParam(required = false) LocalDate date,
                                                                   @RequestParam(required = false) Integer year,
                                                                   @RequestParam(required = false) Integer month) {

        return ResponseEntity.ok(fleetPerformanceService.getFleetRunHours(deviceId, filter, date, year, month));
    }

    @GetMapping("/trips")
    public ResponseEntity<FleetTripTimelineDTO> getFleetTrips(@RequestParam String deviceId,
                                                              @RequestParam String filter,          // only TODAY | DAY
                                                              @RequestParam(required = false) LocalDate date) {

        return ResponseEntity.ok(fleetPerformanceService.getFleetTrips(deviceId, filter, date));
    }

    @GetMapping("/distance-summary")
    public ResponseEntity<FleetPerformanceDTO> getFleetDistanceSummary(@RequestParam String deviceId,
                                                                       @RequestParam String filter,          // WEEK | MONTH
                                                                       @RequestParam(required = false) LocalDate date,
                                                                       @RequestParam(required = false) Integer year,
                                                                       @RequestParam(required = false) Integer month) {

        return ResponseEntity.ok(fleetPerformanceService.getFleetDistanceSummary(deviceId, filter, date, year, month));
    }
}
