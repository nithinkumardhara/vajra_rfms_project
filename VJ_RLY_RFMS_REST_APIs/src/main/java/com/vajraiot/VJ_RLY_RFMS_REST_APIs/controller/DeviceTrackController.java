package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.DeviceTrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
@Slf4j
public class DeviceTrackController {

    private final DeviceTrackService deviceTrackService;

    @GetMapping("/live")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLiveGPS(@RequestParam String deviceId) {
        return ResponseEntity.ok(deviceTrackService.getLiveGPS(deviceId));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHistoryGPS(
            @RequestParam String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        return ResponseEntity.ok(deviceTrackService.getHistoryGPS(deviceId, startTime, endTime));
    }
}
