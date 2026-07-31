package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Device;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepo  deviceRepo;
    
    public ApiResponse<Map<String, Object>> getAllDevices() {
        try {
            List<Device> devices = deviceRepo.findAll();

            Map<String, Object> response = new HashMap<>();

            response.put("totalDevices", devices.size());
            response.put("devices", devices);

            return new ApiResponse<>(true, "Devices retrieved successfully", response);
        } catch (Exception e) {
            log.error("Error retrieving devices", e);
            return new ApiResponse<>(false, "Error : " + e.getMessage(), null);
        }
    }

    public ApiResponse<Device> getDeviceById(String deviceId) {
        try {
            Optional<Device> device = deviceRepo.findByDeviceId(deviceId);

            if (device.isPresent()) {
                return new ApiResponse<>(true, "Device found", device.get());
            }

            return new ApiResponse<>(false, "Device not found", null);
        } catch (Exception e) {
            log.error("Error retrieving device : {}", deviceId, e);

            return new ApiResponse<>(false, "Error : " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<String>> getAllDeviceIds() {
        try {
            List<String> deviceIds = deviceRepo.findAll()
                            .stream()
                            .map(Device::getDeviceId)
                            .toList();

            return new ApiResponse<>(true, "Device IDs retrieved successfully", deviceIds);
        } catch (Exception e) {
            log.error("Error retrieving device IDs", e);

            return new ApiResponse<>(false, "Error : " + e.getMessage(), null);
        }
    }
}
