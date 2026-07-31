package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FleetAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fleet")
@RequiredArgsConstructor
@Slf4j
public class FleetAnalyticsController {

    private final FleetAnalyticsService fleetAnalyticsService;

    //    Get GPS analytics for a device
    @GetMapping("/gps")
    public ResponseEntity<ApiResponse<?>> getGPSAnalytics(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        try {
            //Default Today
            if (startTime == null || endTime == null) {
                endTime = LocalDateTime.now();
                startTime = LocalDate.now().atStartOfDay();
            }

            if (deviceId != null && !deviceId.isBlank()) {
                FleetAnalyticsDTO analytics = fleetAnalyticsService.getFleetAnalytics(deviceId, startTime, endTime, true);

                return ResponseEntity.ok(new ApiResponse<>(true, "GPS analytics retrieved successfully", analytics));
            }

            List<FleetAnalyticsDTO> analyticsList = fleetAnalyticsService.getAllDeviceAnalytics(startTime, endTime);

            return ResponseEntity.ok(new ApiResponse<>(true, "Fleet analytics retrieved successfully", analyticsList));

        } catch (Exception e) {
            log.error("Error fetching GPS analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    //    Get engine mode analysis
    @GetMapping("/engine-mode/{deviceId}")
    public ResponseEntity<ApiResponse<EngineModeDTO>> getEngineModeAnalysis(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "DAILY") String period ) {
        try {
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = switch (period.toUpperCase()) {
                case "WEEKLY" -> endTime.minusDays(7);
                case "MONTHLY" -> endTime.minusMonths(1);
                default -> endTime.minusDays(1);
            };

            EngineModeDTO analysis = fleetAnalyticsService.getEngineModeAnalysis(deviceId, startTime, endTime, period);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Engine mode analysis retrieved successfully", analysis));

        } catch (Exception e) {
            log.error("Error fetching engine mode analysis for device: {}", deviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

}
