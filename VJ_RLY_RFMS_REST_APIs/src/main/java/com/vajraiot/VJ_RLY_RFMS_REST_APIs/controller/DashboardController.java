package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.DashboardSummaryDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

//    Get vehicle summary count
    @GetMapping("/summary")
    @Cacheable(value = "dashboardSummary", unless = "#result == null")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary() {
        try {
            DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Dashboard summary retrieved successfully", summary)
            );

        } catch (Exception e) {
            log.error("Error fetching dashboard summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

//    Get devices filtered by status
    @GetMapping("/devices/{status}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDevicesByStatus(@RequestParam(required = false) String status) {
        try {
            Map<String, Object> response = dashboardService.getDevicesByStatus(status);

            if (response.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "No devices found with status: " + status, null));
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Devices retrieved with status: " + status, response)
            );

        } catch (Exception e) {
            log.error("Error fetching devices by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

}
