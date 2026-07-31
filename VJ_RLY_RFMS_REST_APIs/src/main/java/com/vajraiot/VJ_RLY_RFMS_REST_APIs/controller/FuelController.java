package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.MonthlyMetricDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FuelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@RestController
@RequestMapping("/api/fuel")
@RequiredArgsConstructor
@Slf4j
public class FuelController {

    private final FuelService fuelService;

    // Last 24 hours fuel consumed
    @GetMapping("/consumption")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConsumption( @RequestParam(required = false) String deviceId) {

        List<Map<String, Object>> data = fuelService.get24HoursConsumption(deviceId);

        ApiResponse<List<Map<String, Object>>> response =
                ApiResponse.success("last 24 hours fuel consumption", data);

        return ResponseEntity.ok(response);
    }

    //Monthly Distance & fuel Summary
    @GetMapping("/device/month")
    public ResponseEntity<ApiResponse<List<MonthlyMetricDTO>>> getMonthlyMetrics(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        if (year == null) {
            year = Year.now().getValue();
        }

        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        List<MonthlyMetricDTO> metrics = fuelService.getMonthlyMetrics(deviceId, year, month);

        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly metrics retrieved successfully", metrics));
    }

}