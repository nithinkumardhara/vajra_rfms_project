package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.DashboardSummaryDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Device;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceDataSnapshot;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final DeviceRepo deviceRepo;
    private final DeviceDataSnapshotRepo snapshotRepo;

//    Get dashboard summary with all vehicle counts
    public DashboardSummaryDTO getDashboardSummary() {
        try {
            List<Device> allDevices = deviceRepo.findAll();
            List<DeviceDataSnapshot> snapshots = snapshotRepo.findAll();

            // Count by communication status
            long communicating = allDevices.stream()
                    .filter(this::isCommunicating)
                    .count();

            long nonCommunicating = allDevices.size() - communicating;

            // Count by movement status from snapshots
            long moving = snapshots.stream()
                    .filter(s -> "MOVING".equals(s.getFleetState()))
                    .count();

            long stopped = snapshots.stream()
                    .filter(s -> "STOPPED".equals(s.getFleetState()))
                    .count();

            long idle = snapshots.stream()
                    .filter(s -> "IDLE".equals(s.getFleetState()))
                    .count();

            return DashboardSummaryDTO.builder()
                    .communicating(communicating)
                    .nonCommunicating(nonCommunicating)
                    .moving(moving)
                    .stopped(stopped)
                    .idle(idle)
                    .build();

        } catch (Exception e) {
            log.error("Error calculating dashboard summary", e);
            throw e;
        }
    }


//    Get devices Status: MOVING, STOPPED, IDLE, HIBERNATING, COMMUNICATING, NON_COMMUNICATING
    public Map<String, Object> getDevicesByStatus(String status) {
        try {
            List<Device> devices;

            if (status == null || status.trim().isEmpty()) {
                devices = deviceRepo.findAll();
                status = "ALL";
            } else {
                switch (status.toUpperCase()) {
                    case "COMMUNICATING":
                        devices = deviceRepo.findAll()
                                .stream()
                                .filter(this::isCommunicating)
                                .collect(Collectors.toList());
                        break;

                    case "NON_COMMUNICATING":
                        devices = deviceRepo.findAll()
                                .stream()
                                .filter(d -> !isCommunicating(d))
                                .collect(Collectors.toList());
                        break;

                    case "MOVING":
                    case "STOPPED":
                    case "IDLE":
                    case "HIBERNATING":
                        devices = getDevicesByMovementStatus(status);
                        break;

                    default:
                        devices = deviceRepo.findAll();
                        status = "ALL";
                        break;
                }
            }

            List<Map<String, Object>> enhancedDevices = devices.stream()
                    .map(this::enrichDeviceData)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("deviceCount", devices.size());
            response.put("devices", enhancedDevices);
            response.put("status", status.toUpperCase());
            response.put("timestamp", LocalDateTime.now());

            return response;

        } catch (Exception e) {
            log.error("Error fetching devices by status: {}", status, e);
            throw e;
        }
    }

    private boolean isCommunicating(Device device) {
        if (device.getLastCommunicationTime() == null) {
            return false;
        }

        return device.getLastCommunicationTime().isAfter(LocalDateTime.now().minusHours(4));
    }

//    Helper method: Get devices by movement status
    private List<Device> getDevicesByMovementStatus(String status) {
        List<DeviceDataSnapshot> snapshots = snapshotRepo.findAll();

        List<String> deviceIds = new ArrayList<>();

        switch (status.toUpperCase()) {
            case "MOVING":
                deviceIds = snapshots.stream()
                        .filter(s -> "MOVING".equals(s.getFleetState()))
                        .map(DeviceDataSnapshot::getDeviceId)
                        .collect(Collectors.toList());
                break;

            case "STOPPED":
                deviceIds = snapshots.stream()
                        .filter(s -> "STOPPED".equals(s.getFleetState()))
                        .map(DeviceDataSnapshot::getDeviceId)
                        .collect(Collectors.toList());
                break;

            case "IDLE":
                deviceIds = snapshots.stream()
                        .filter(s -> "IDLE".equals(s.getFleetState()))
                        .map(DeviceDataSnapshot::getDeviceId)
                        .collect(Collectors.toList());
                break;
        }

        if (deviceIds.isEmpty()) {
            return new ArrayList<>();
        }

        return deviceRepo.findByDeviceIdIn(deviceIds);
    }

//    Enrich device with latest snapshot data
    private Map<String, Object> enrichDeviceData(Device device) {
        Map<String, Object> enriched = new HashMap<>();

        enriched.put("deviceId", device.getDeviceId());
        enriched.put("imei", device.getImei());
        enriched.put("lastCommunicationTime", device.getLastCommunicationTime());

        // Get latest snapshot
        Optional<DeviceDataSnapshot> snapshot = snapshotRepo.findByDeviceId(device.getDeviceId());
        if (snapshot.isPresent()) {
            DeviceDataSnapshot s = snapshot.get();
            enriched.put("latitude", s.getLatitude());
            enriched.put("longitude", s.getLongitude());
            enriched.put("speed", s.getSpeed());
            enriched.put("fuelLevel", s.getFuelLevel());
            enriched.put("fuelLevelPercentage", s.getFuelLevelPercentage());
            enriched.put("fuelHeight", s.getFuelHeight());
            enriched.put("status", s.getFleetState());
            enriched.put("temperature", s.getTemperature());
            enriched.put("runHours", s.getRunHours());
        }

        return enriched;
    }

}
