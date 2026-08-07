package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FuelPerformanceDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FuelReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/fuel")
@RequiredArgsConstructor
@Slf4j
public class FuelReportController {

    private final FuelReportService fuelReportService;

    @GetMapping("/today")
    public ResponseEntity<FuelPerformanceDTO> getTodayReport(@RequestParam(required = false) String deviceId) {
        FuelPerformanceDTO report = fuelReportService.getTodayReport(deviceId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/day")
    public ResponseEntity<FuelPerformanceDTO> getDayWiseReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                               @RequestParam(required = false) String deviceId) {
        FuelPerformanceDTO report = fuelReportService.getDayWiseReport(date, deviceId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/week")
    public ResponseEntity<FuelPerformanceDTO> getWeeklyReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                              @RequestParam(required = false) String deviceId) {
        FuelPerformanceDTO report = fuelReportService.getWeeklyReport(date, deviceId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/month")
    public ResponseEntity<FuelPerformanceDTO> getMonthlyReport(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
                                                               @RequestParam(required = false) String deviceId) {
        FuelPerformanceDTO report = fuelReportService.getMonthlyReport(yearMonth, deviceId);
        return ResponseEntity.ok(report);
    }

//    @GetMapping("/custom")
//    public ResponseEntity<FuelPerformanceDTO> getCustomReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime from,
//                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime to,
//                                                              @RequestParam(required = false) String deviceId) {
//        FuelPerformanceDTO report = fuelReportService.getReport(from, to, deviceId);
//        return ResponseEntity.ok(report);
//    }

}