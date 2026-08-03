package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceDataSnapshot;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceDataSnapshotRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceGPSDataRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTrackService {

    private final DeviceDataSnapshotRepo  deviceDataSnapshotRepo;
    private final DeviceGPSDataRepo deviceGPSDataRepo;

    public ApiResponse<Map<String, Object>> getLiveGPS(String deviceId) {
        try {
            Optional<DeviceDataSnapshot> loc = deviceDataSnapshotRepo.findByDeviceId(deviceId);

            Map<String, Object> response = new HashMap<>();

            if (loc.isPresent()) {
                DeviceDataSnapshot data = loc.get();

                response.put("deviceId", deviceId);
                response.put("latitude", data.getLatitude());
                response.put("longitude", data.getLongitude());
                response.put("speed", data.getSpeed());
                response.put("status", data.getFleetState());
                response.put("heading", data.getHeading());
                response.put("fuelLevel", data.getFuelLevel());
                response.put("IgnitionOn", data.getIsIgnitionON());
                response.put("Temperature", data.getTemperature());
                response.put("packetTimestamp", data.getPacketTimestamp());
            }

            return new ApiResponse<>(true, "LiveGPS retrieved successfully", response);
        } catch (Exception e) {
            log.error("Error retrieving LiveGPS", e);
            return new ApiResponse<>(false, "Error : " + e.getMessage(), null);
        }
    }

    public ApiResponse<Map<String, Object>> getHistoryGPS(String deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<DeviceGPSData> locations = deviceGPSDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(
                                    deviceId,
                                    startTime,
                                    endTime
                            );

            List<Map<String, Object>> routePoints = locations.stream()
                            .map(loc -> {
                                Map<String, Object> point = new HashMap<>();
                                point.put("latitude", loc.getLatitude());
                                point.put("longitude", loc.getLongitude());
                                point.put("latitudeDirection", loc.getLatitudeDirection());
                                point.put("longitudeDirection", loc.getLongitudeDirection());
                                point.put("speed", loc.getSpeed());
                                point.put("status", loc.getFleetState());
                                point.put("heading", loc.getHeading());
                                point.put("timestamp", loc.getPacketTimestamp());
                                return point;
                            }).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("deviceId", deviceId);
            response.put("startTime", startTime);
            response.put("endTime", endTime);
            response.put("totalPoints", routePoints.size());
            response.put("route", routePoints);

            return new ApiResponse<>(true, "History GPS retrieved successfully", response);

        } catch (Exception e) {
            log.error("Error retrieving HistoryGPS", e);
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }
}
