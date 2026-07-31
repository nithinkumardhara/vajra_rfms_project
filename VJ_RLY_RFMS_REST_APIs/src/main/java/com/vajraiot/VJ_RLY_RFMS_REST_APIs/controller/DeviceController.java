package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Device;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.DeviceService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllDevices() {

        ApiResponse<Map<String,Object>> response = deviceService.getAllDevices();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{deviceId}")
    public ResponseEntity<ApiResponse<Device>> getDeviceById(@PathVariable String deviceId) {

        ApiResponse<Device> response = deviceService.getDeviceById(deviceId);

        if(response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }


    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<String>>> getAllDeviceIds() {

        ApiResponse<List<String>> response = deviceService.getAllDeviceIds();

        return ResponseEntity.ok(response);
    }
}