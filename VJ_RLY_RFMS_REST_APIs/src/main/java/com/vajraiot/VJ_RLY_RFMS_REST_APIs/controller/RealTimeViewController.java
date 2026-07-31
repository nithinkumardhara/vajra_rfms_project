package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceDataSnapshot;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceDataSnapshotRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FuelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
@Slf4j
public class RealTimeViewController {

    private final DeviceDataSnapshotRepo snapshotRepo;
    private final FuelService fuelService;

    @GetMapping("/{deviceId}/status")
    @Cacheable(value = "deviceStatus", key = "#deviceId", unless = "#result == null")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDeviceStatus(@PathVariable String deviceId) {
        try {
            Optional<DeviceDataSnapshot> snapshot = snapshotRepo.findByDeviceId(deviceId);

            if (snapshot.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Device status not found", null));
            }

            Map<String, Object> response = new HashMap<>();

            response.put("liveData", snapshot.get());
            response.put("last24HoursConsumption", fuelService.get24HoursConsumption(deviceId));

            return ResponseEntity.ok(new ApiResponse<>(true, "Device status retrieved", response));

        } catch (Exception e) {
            log.error("Error retrieving device status: {}", deviceId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

}
