package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FuelStatisticsDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.MonthlyMetricDTO;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceFuelData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.util.DistanceCalculateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelService {

    private final DeviceFuelDataRepo fuelDataRepo;
    private final DeviceGPSDataRepo gpsDataRepo;
    private final DeviceRepo deviceRepo;

    // Monthly Distance & Fuel Report metrics
    public List<MonthlyMetricDTO> getMonthlyMetrics(String deviceId, Integer year, Integer month) {

        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        // Specific device
        if (deviceId != null && !deviceId.isBlank()) {
            MonthlyMetricDTO dto = calculateMonthlyMetrics(deviceId, startOfMonth, endOfMonth);

            return Collections.singletonList(dto);
        }
        // All devices
        return calculateAllDevicesMonthlyMetrics(startOfMonth, endOfMonth);
    }

    private List<MonthlyMetricDTO> calculateAllDevicesMonthlyMetrics(LocalDateTime startTime, LocalDateTime endTime) {

        List<String> deviceIds = deviceRepo.findAllDistinctDeviceIds();

        List<MonthlyMetricDTO> result = new ArrayList<>();

        for (String deviceId : deviceIds) {
            MonthlyMetricDTO metrics = calculateMonthlyMetrics(deviceId, startTime, endTime);

            if (metrics != null) {
                result.add(metrics);
            }
        }
        return result;
    }

    private MonthlyMetricDTO calculateMonthlyMetrics(String deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            MonthlyMetricDTO metrics = new MonthlyMetricDTO();
            metrics.setDeviceId(deviceId);

            List<DeviceGPSData> gpsDataList = gpsDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(deviceId, startTime, endTime);

            if (!gpsDataList.isEmpty()) {
                gpsDataList.sort(Comparator.comparing(DeviceGPSData::getPacketTimestamp));
                // Calculate distance
                Double distance = DistanceCalculateUtil.calculateDistance(gpsDataList);
                metrics.setDistance(distance);
            } else {
                metrics.setDistance(0.0);
            }

            List<DeviceFuelData> fuelDataList = fuelDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(deviceId, startTime, endTime);

            if (!fuelDataList.isEmpty()) {
                FuelStatisticsDTO statistics = calculateFuelStatistics(fuelDataList);

                metrics.setFuelConsumed(statistics.getFuelConsumed());
                metrics.setFuelFill(statistics.getFuelFilled());
            } else {
                metrics.setFuelConsumed(0.0);
                metrics.setFuelFill(0.0);
            }

            return metrics;
        } catch (Exception e) {
            log.error("Error calculating monthly metrics", e);
            return null;
        }
    }

    // Last 24 hours fuel consumptions
    public List<Map<String, Object>> get24HoursConsumption(String deviceId) {

        LocalDateTime start = LocalDateTime.now().minusHours(24);

        List<String> deviceIds;
        if (deviceId != null && !deviceId.isBlank()) {
            deviceIds = List.of(deviceId);
        } else {
            deviceIds = deviceRepo.findAllDistinctDeviceIds();
        }

        List<DeviceFuelData> records;
        if (deviceId != null && !deviceId.isBlank()) {
            records = fuelDataRepo.findLast24HoursDataByDeviceId(deviceId, start);
        } else {
            records = fuelDataRepo.findLast24HoursData(start);
        }

        // Latest packet
        List<DeviceFuelData> latestRecords;
        if (deviceId != null && !deviceId.isBlank()) {
            DeviceFuelData latest = fuelDataRepo.findLatestPacketByDeviceId(deviceId);
            latestRecords = latest != null ? List.of(latest) : Collections.emptyList();
        } else {
            latestRecords = fuelDataRepo.findLatestPacketForDevices(deviceIds);
        }

        Map<String, List<DeviceFuelData>> deviceFuelMap = records.stream()
                        .collect(Collectors.groupingBy(DeviceFuelData::getDeviceId));
        Map<String, DeviceFuelData> latestPacketMap = new HashMap<>();

        for (DeviceFuelData latest : latestRecords) {
            latestPacketMap.put(latest.getDeviceId(), latest);
        }

        Map<String, FuelStatisticsDTO> statisticsMap = new HashMap<>();

        for (Map.Entry<String, List<DeviceFuelData>> entry : deviceFuelMap.entrySet()) {
            statisticsMap.put(entry.getKey(), calculateFuelStatistics(entry.getValue()));
        }

        return deviceIds.stream()
                .map(id -> {
                    DeviceFuelData latest = latestPacketMap.get(id);
                    FuelStatisticsDTO statistics = statisticsMap.getOrDefault(id, new FuelStatisticsDTO());
                    Map<String, Object> response = new HashMap<>();
                    response.put("deviceId", id);
                    response.put("fuelConsumed", statistics.getFuelConsumed());
                    response.put("currentFuelLevel", latest != null ? latest.getFuelLevel() : 0.0);

                    return response;
                }).toList();
    }


    private FuelStatisticsDTO calculateFuelStatistics(List<DeviceFuelData> fuelDataList) {

        if (fuelDataList == null || fuelDataList.size() < 2) {
            return new FuelStatisticsDTO(0.0, 0.0, 0.0);
        }

        fuelDataList.sort(Comparator.comparing(DeviceFuelData::getPacketTimestamp));

        double totalConsumption = 0.0;
        double totalRefill = 0.0;
        double totalTheft = 0.0;

        for (int i = 1; i < fuelDataList.size(); i++) {

            DeviceFuelData previous = fuelDataList.get(i - 1);
            DeviceFuelData current = fuelDataList.get(i);

            if (previous.getFuelLevel() == null || current.getFuelLevel() == null) {
                continue;
            }

            double diff = current.getFuelLevel() - previous.getFuelLevel();

            if (diff > 0) {
                totalRefill += diff;
            } else if (diff < 0) {
                double drop = Math.abs(diff);

                totalConsumption += drop;

                // Theft Logic If Need
            }
        }

        return new FuelStatisticsDTO(totalConsumption, totalRefill, totalTheft);
    }

}