package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceFuelData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceAlarmStatusRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceFuelDataRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceGPSDataRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.util.DistanceCalculateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelAnalyticsService {

    private final DeviceFuelDataRepo fuelRepo;
    private final DeviceGPSDataRepo gpsRepo;
    private final DeviceAlarmStatusRepo alarmRepo;
    private final DeviceRepo deviceRepo;

    private static final double REFILL_THRESHOLD = 3.0;
    private static final double THEFT_THRESHOLD = 1.0;

    public List<FuelReportDTO> getReport(String deviceId, String reportType, LocalDate date, YearMonth month, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime start;
        LocalDateTime end;

        switch (reportType.toUpperCase()) {
            case "DAILY":
                // If date is not provided, use today's logic
                if (date == null) {
                    start = LocalDate.now().atStartOfDay();
                    end = LocalDateTime.now();
                } else {
                    start = date.atStartOfDay();
                    end = date.atTime(23, 59, 59);
                }
                break;

            case "WEEKLY":
                start = LocalDate.now().minusDays(7).atStartOfDay();
                end = LocalDateTime.now();
                break;

            case "MONTHLY":
                if (month == null) {
                    month = YearMonth.now().minusMonths(1);
                }
                start = month.atDay(1).atStartOfDay();
                end = month.atEndOfMonth().atTime(23, 59, 59);
                break;

            case "CUSTOM":
                if (startDateTime == null || endDateTime == null) {
                    throw new IllegalArgumentException("startDateTime and endDateTime are required");
                }

                if (endDateTime.isBefore(startDateTime)) {
                    throw new IllegalArgumentException("endDateTime cannot be before startDateTime");
                }

                if (endDateTime.isAfter(startDateTime.plusMonths(1))) {
                    throw new IllegalArgumentException("Custom report range cannot exceed 1 month");
                }

                start = startDateTime;
                end = endDateTime;
                break;

            default:
                throw new IllegalArgumentException("Invalid reportType");
        }

        return generateReports(deviceId, start, end, reportType);
    }

    private List<FuelReportDTO> generateReports(String deviceId, LocalDateTime start, LocalDateTime end, String reportType) {
        boolean detailed = deviceId != null && !deviceId.isBlank();

        List<String> deviceIds;

        if (detailed) {
            deviceIds = List.of(deviceId);
        } else {
            deviceIds = deviceRepo.findAllDistinctDeviceIds();
        }

        return deviceIds.stream()
                .map(id -> generateFuelReport(id, start, end, reportType, detailed))
                .filter(report -> report.getDeviceId() != null)
                .toList();
    }

    private FuelReportDTO generateFuelReport(String deviceId, LocalDateTime start, LocalDateTime end, String reportType, boolean detailed) {

        List<DeviceFuelData> fuelData = fuelRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(
                        deviceId,
                        start,
                        end);

        if (fuelData.isEmpty()) {
            return FuelReportDTO.builder().build();
        }

        List<DeviceGPSData> gpsData = detailed
                ? gpsRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(
                deviceId,
                start,
                end)
                : List.of();

        List<DeviceAlarmStatus> alarms = detailed
                ? alarmRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(
                deviceId,
                start,
                end)
                : List.of();

        FuelAnalysisResultDTO result = analyzeFuelEvents(fuelData, gpsData, alarms, detailed);

        DeviceFuelData opening = fuelData.get(0);
        DeviceFuelData closing = fuelData.get(fuelData.size() - 1);

        FuelReportDTO.FuelReportDTOBuilder builder = FuelReportDTO.builder()
                        .reportType(reportType)
                        .deviceId(deviceId)
                        .startDateTime(start)
                        .endDateTime(end)
                        .openingFuelLevel(opening.getFuelLevel())
                        .closingFuelLevel(closing.getFuelLevel())
                        .totalConsumption(result.getConsumption())
                        .refillCount(result.getRefillCount())
                        .totalRefillQuantity(result.getTotalRefill())
                        .theftCount(result.getTheftCount())
                        .theftQuantity(result.getTotalTheft())
                        .runningHours(closing.getRunHours());

        if(detailed){
            builder.distanceTraveled(DistanceCalculateUtil.calculateDistance(gpsData))
                    .refillEvents(result.getRefillEvents())
                    .theftEvents(result.getTheftEvents())
                    .fuelReports(result.getFuelRecords());
        }

        return builder.build();
    }

    private FuelAnalysisResultDTO analyzeFuelEvents(
            List<DeviceFuelData> fuelData,
            List<DeviceGPSData> gpsData,
            List<DeviceAlarmStatus> alarms,
            boolean detailed) {

        List<RefillEventDTO> refillEvents = new ArrayList<>();
        List<TheftEventDTO> theftEvents = new ArrayList<>();
        List<FuelRecordDTO> records = new ArrayList<>();

        double totalConsumption = 0;
        double totalRefill = 0;
        double totalTheft = 0;
        int refillCount = 0;
        int theftCount = 0;

        for (int i = 1; i < fuelData.size(); i++) {

            DeviceFuelData prev = fuelData.get(i - 1);
            DeviceFuelData curr = fuelData.get(i);

            if (prev.getFuelLevel() == null || curr.getFuelLevel() == null)
                continue;

            double diff = curr.getFuelLevel() - prev.getFuelLevel();

            DeviceGPSData gps = detailed ? findNearestGPS(gpsData, curr.getPacketTimestamp()) : null;

            DeviceAlarmStatus alarm = detailed ? findNearestAlarm(alarms, curr.getPacketTimestamp()) : null;

            boolean ignition = alarm != null && Boolean.TRUE.equals(alarm.getIsIgnitionON());
            boolean speed = gps != null && gps.getSpeed() <= 5.0;

            String eventType = "NORMAL";

            if (diff > 0) {

                if ((alarm != null && Boolean.TRUE.equals(alarm.getIsFuelRefillDetected())) || diff >= REFILL_THRESHOLD) {
                    eventType = "REFILL";

                    totalRefill += diff;

                    refillCount++;

                    if (detailed) {
                        refillEvents.add(
                                RefillEventDTO.builder()
                                        .timestamp(curr.getPacketTimestamp())
                                        .beforeLevel(prev.getFuelLevel())
                                        .afterLevel(curr.getFuelLevel())
                                        .refillQuantity(diff)
                                        .latitude(gps != null ? gps.getLatitude() : null)
                                        .longitude(gps != null ? gps.getLongitude() : null)
                                        .durationMinutes(getDurationMinutes(prev, curr))
                                        .build()
                        );
                    }
                }
            }

            else if (diff < 0) {

                double drop = Math.abs(diff);

                boolean theftAlarm = alarm != null && Boolean.TRUE.equals(alarm.getIsFuelTheftAlert());

                boolean theftCondition = theftAlarm || (!ignition && drop > 0) || (ignition && speed && drop > THEFT_THRESHOLD);

                if (theftCondition) {
                    eventType = "THEFT";

                    totalTheft += drop;

                    theftCount++;

                    if(detailed){
                    theftEvents.add(
                            TheftEventDTO.builder()
                                    .timestamp(curr.getPacketTimestamp())
                                    .fuelLevelBefore(prev.getFuelLevel())
                                    .fuelLevelAfter(curr.getFuelLevel())
                                    .theftQuantity(drop)
                                    .fuelConsumption(drop)
                                    .latitude(gps != null ? gps.getLatitude() : null)
                                    .longitude(gps != null ? gps.getLongitude() : null)
                                    .speed(gps != null ? gps.getSpeed() : null)
                                    .durationMinutes(getDurationMinutes(prev, curr))
                                    .IgnitionON(ignition)
                                    .build()
                    );
                }
                    }
                else {
                    eventType = "NORMAL_CONSUMPTION";
                    totalConsumption += drop;
                }
            }

            if(detailed){
                records.add(FuelRecordDTO.builder()
                                .timestamp(curr.getPacketTimestamp())
                                .fuelLevel(curr.getFuelLevel())
                                .eventType(eventType)
                                .ignitionOn(ignition)
                                .latitude(gps != null ? gps.getLatitude() : null)
                                .longitude(gps != null ? gps.getLongitude() : null)
                                .speed(gps != null ? gps.getSpeed() : null)
                                .build()
                );
            }
        }

        return FuelAnalysisResultDTO.builder()
                .consumption(totalConsumption)
                .totalRefill(totalRefill)
                .totalTheft(totalTheft)
                .refillCount(refillCount)
                .theftCount(theftCount)
                .refillEvents(refillEvents)
                .theftEvents(theftEvents)
                .fuelRecords(records)
                .build();
    }


    private double getDurationMinutes(DeviceFuelData prev, DeviceFuelData curr) {
        return Math.abs(Duration.between(
                                prev.getPacketTimestamp(),
                                curr.getPacketTimestamp())
                        .toMinutes());
    }

    private DeviceGPSData findNearestGPS(List<DeviceGPSData> gpsData, LocalDateTime timestamp) {

        return gpsData.stream()
                .min(
                        Comparator.comparingLong(
                                gps -> Math.abs(
                                        Duration.between(
                                                gps.getPacketTimestamp(),
                                                timestamp
                                        ).toSeconds()
                                )
                        )
                )
                .orElse(null);
    }

    private DeviceAlarmStatus findNearestAlarm(List<DeviceAlarmStatus> alarms, LocalDateTime timestamp) {

        return alarms.stream()
                .min(
                        Comparator.comparingLong(
                                alarm -> Math.abs(
                                        Duration.between(
                                                alarm.getPacketTimestamp(),
                                                timestamp
                                        ).toSeconds()
                                )
                        )
                )
                .orElse(null);
    }

}