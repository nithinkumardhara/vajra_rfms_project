package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.util.DistanceCalculateUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FleetAnalyticsService {

    private final DeviceGPSDataRepo gpsDataRepo;
    private final DeviceAlarmStatusRepo alarmStatusRepo;
    private final DeviceRepo deviceRepo;

    //    Get GPS analytics summary
    public List<FleetAnalyticsDTO> getAllDeviceAnalytics(LocalDateTime startTime, LocalDateTime endTime) {

        List<String> deviceIds = deviceRepo.findAllDistinctDeviceIds();

        return deviceIds.stream()
                .map(deviceId -> getFleetAnalytics(deviceId, startTime, endTime, false))
                .toList();
    }

    //    Get GPS analytics for a device
    public FleetAnalyticsDTO getFleetAnalytics(String deviceId, LocalDateTime startTime, LocalDateTime endTime , boolean includeRecords) {
    try {
        FleetAnalyticsDTO analytics = FleetAnalyticsDTO.builder()
                .deviceId(deviceId)
                .build();

        List<DeviceGPSData> gpsDataList = gpsDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(deviceId, startTime, endTime);

        Collections.reverse(gpsDataList);

        if (gpsDataList.isEmpty()) {
            return analytics;
        }

        // Current GPS Details
        DeviceGPSData lastRecord = gpsDataList.getLast();

        analytics.setCurrentLatitude(lastRecord.getLatitude());
        analytics.setCurrentLongitude(lastRecord.getLongitude());
        analytics.setMovementStatus(lastRecord.getMovementStatus());

        // Distance Calculation
        Double totalDistance = DistanceCalculateUtil.calculateDistance(gpsDataList);

        analytics.setTotalDistanceTraveled(totalDistance);

        // Average Speed
        Double averageSpeed = gpsDataList.stream()
                .filter(g -> g.getSpeed() != null)
                .mapToDouble(DeviceGPSData::getSpeed)
                .average()
                .orElse(0.0);

        analytics.setAverageSpeed(averageSpeed);

        // Moving / Idle / Stop Analysis
        int movingCount = 0;
        int idleCount = 0;
        int stopCount = 0;

        long movingDuration = 0;
        long idleDuration = 0;
        long stopDuration = 0;

        for (int i = 1; i < gpsDataList.size(); i++) {
            DeviceGPSData previous = gpsDataList.get(i - 1);
            DeviceGPSData current = gpsDataList.get(i);

            long durationSeconds = ChronoUnit.SECONDS.between(current.getPacketTimestamp(), previous.getPacketTimestamp());

            String status = current.getMovementStatus() != null ? current.getMovementStatus().trim().toUpperCase() : "";

            switch (status) {
                case "MOVING":
                    movingCount++;
                    movingDuration += durationSeconds;
                    break;

                case "IDLE":
                    idleCount++;
                    idleDuration += durationSeconds;
                    break;

                case "STOPPED":
                    stopCount++;
                    stopDuration += durationSeconds;
                    break;

                default:
                    break;
            }
        }

        analytics.setTotalMoving(movingCount);
        analytics.setTotalIdles(idleCount);
        analytics.setTotalStopped(stopCount);

        analytics.setTotalMovingDuration(formatHours(movingDuration));
        analytics.setTotalIdleDuration(formatHours(idleDuration));
        analytics.setTotalStoppedDuration(formatHours(stopDuration));

        // GPS Records List
        if (includeRecords){
            List<GPSRecordDTO> records = gpsDataList.stream()
                    .map(gps -> GPSRecordDTO.builder()
                            .id(gps.getId())
                            .deviceId(gps.getDeviceId())
                            .gpsFix(gps.getGpsFix())
                            .latitude(gps.getLatitude())
                            .longitude(gps.getLongitude())
                            .speed(gps.getSpeed())
                            .heading(gps.getHeading())
                            .altitude(gps.getAltitude())
                            .noOfSatellites(gps.getNoOfSatellites())
                            .pdop(gps.getPdop())
                            .hdop(gps.getHdop())
                            .movementStatus(gps.getMovementStatus())
                            .packetTimestamp(gps.getPacketTimestamp())
                            .build())
                    .toList();

        analytics.setDataPointRecords(records);
    }
        return analytics;

    } catch (Exception e) {
        log.error("Error calculating fleet analytics for device {}", deviceId, e);
        throw e;
    }
}

//    Get engine mode analysis for device
    public EngineModeDTO getEngineModeAnalysis(String deviceId, LocalDateTime startTime, LocalDateTime endTime, String period) {
        try {
            EngineModeDTO analysis = EngineModeDTO.builder()
                    .deviceId(deviceId)
                    .period(period)
                    .startDate(startTime)
                    .endDate(endTime)
                    .build();

            // Get alarm data for engine status
            List<DeviceAlarmStatus> alarmDataList = alarmStatusRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(
                            deviceId, startTime, endTime);
            Collections.reverse(alarmDataList);

            if (alarmDataList.isEmpty()) {
                return analysis;
            }

            // Time Engine ON/OFF
            long engineOnTime = 0;
            long engineOffTime = 0;
            int toggleCount = 0;

            for (int i = 1; i < alarmDataList.size(); i++) {
                DeviceAlarmStatus current = alarmDataList.get(i);
                DeviceAlarmStatus previous = alarmDataList.get(i - 1);

                long duration = ChronoUnit.SECONDS.between(previous.getPacketTimestamp(), current.getPacketTimestamp());

                if (Boolean.TRUE.equals(previous.getIsVehicleON())) {
                    engineOnTime += duration;
                } else {
                    engineOffTime += duration;
                }

                // Count toggles
                if (!Objects.equals(previous.getIsVehicleON(), current.getIsVehicleON())) {
                    toggleCount++;
                }
            }

            analysis.setTimeEngineOn(engineOnTime);
            analysis.setTimeEngineOff(engineOffTime);
            analysis.setIgnitionToggleCount(toggleCount);

            // Last Engine Status
            for (int i = alarmDataList.size() - 1; i >= 0; i--) {
                if (Boolean.TRUE.equals(alarmDataList.get(i).getIsVehicleON())) {
                    analysis.setLastIgnitionOnTime(alarmDataList.get(i).getPacketTimestamp());
                    break;
                }
            }

            // Idle Time (GPS data needed)
            List<DeviceGPSData> gpsDataList = gpsDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampDesc(
                            deviceId, startTime, endTime);

            long idleTime = gpsDataList.stream()
                    .filter(g -> "IDLE".equals(g.getMovementStatus()))
                    .count() * 10; // Assuming 10-second intervals

            analysis.setIdleTime(idleTime);

            return analysis;

        } catch (Exception e) {
            log.error("Error analyzing engine mode for device: {}", deviceId, e);
            throw e;
        }
    }

    private String formatHours(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

}
