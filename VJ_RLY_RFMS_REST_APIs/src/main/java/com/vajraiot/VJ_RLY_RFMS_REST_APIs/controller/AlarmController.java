package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.AlarmStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmStatusService alarmStatusService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAlarmStatus(@RequestParam(required = false) String deviceId) {
        return ResponseEntity.ok(alarmStatusService.getLatestAlarmStatus(deviceId));
    }
}
