package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DevicePacketData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DevicePacketDataRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.util.DeviceFuelMetrics;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.util.FuelCalculationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelReportServiceImpl implements FuelReportService {

    private final DevicePacketDataRepo devicePacketDataRepo;
    private final DeviceRepo deviceRepo;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public FuelPerformanceDTO getTodayReport(String deviceId) {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = LocalDateTime.now();
        return buildDayReport(from, to, deviceId, today.format(DATE_FMT));
    }

    @Override
    public FuelPerformanceDTO getDayWiseReport(LocalDate date, String deviceId) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay().minusNanos(1);
        return buildDayReport(from, to, deviceId, date.format(DATE_FMT));
    }

    @Override
    public FuelPerformanceDTO getWeeklyReport(LocalDate date, String deviceIds) {
        LocalDateTime from = date.minusDays(6).atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);
        String range = from.format(DATE_FMT) + " to " + to.format(DATE_FMT);
        return buildPeriodReport(from, to, deviceIds, range);
    }

    @Override
    public FuelPerformanceDTO getMonthlyReport(YearMonth month, String deviceIds) {
        LocalDateTime from = month.atDay(1).atStartOfDay();
        LocalDateTime to = month.atEndOfMonth().plusDays(1).atStartOfDay().minusNanos(1);
        String range = month.toString();
        return buildPeriodReport(from, to, deviceIds, range);
    }

    @Override
    public FuelPerformanceDTO getReport(LocalDateTime from, LocalDateTime to, String deviceIds) {
        String range = from.toLocalDate().format(DATE_FMT) + " to " + to.toLocalDate().format(DATE_FMT);
        // Decide shape by duration
        long days = Duration.between(from, to).toDays();
        if (days <= 1) {
            return buildDayReport(from, to, deviceIds, range);
        }
        return buildPeriodReport(from, to, deviceIds, range);
    }

    // TODAY | DAY-WISE
    private FuelPerformanceDTO buildDayReport(LocalDateTime from, LocalDateTime to, String deviceIdParam, String range) {

        List<String> deviceIds = resolveDeviceIds(deviceIdParam);
        if (deviceIds.isEmpty()) {
            return emptyDayReport(range);
        }

        List<DevicePacketData> packets = devicePacketDataRepo
                .findByDeviceIdInAndPacketTimestampBetweenOrderByDeviceIdAscPacketTimestampAsc(deviceIds, from, to);

        Map<String, List<DevicePacketData>> byDevice = packets.stream()
                .collect(Collectors.groupingBy(DevicePacketData::getDeviceId,
                        LinkedHashMap::new, Collectors.toList()));

        List<FuelDataDTO> fuelDataList = new ArrayList<>();
        List<FuelEventDTO> allEvents = new ArrayList<>();
        double totalUsed = 0, totalFill = 0, totalTheft = 0;
        double totalDistance = 0, totalRunHours = 0;
        double totalFuelInFleet = 0;

        for (String deviceId : deviceIds) {
            List<DevicePacketData> devicePackets = byDevice.getOrDefault(deviceId, Collections.emptyList());
            if (devicePackets == null || devicePackets.isEmpty()) {
                continue;
            }

            DeviceFuelMetrics metrics = FuelCalculationHelper.calculateDeviceMetrics(devicePackets);

            fuelDataList.add(FuelDataDTO.builder()
                    .deviceId(deviceId)
                    .fuelLevel(metrics.getLatestFuelLevel())
                    .tankCapacity(null)
                    .fuelConsumption(metrics.getFuelConsumption())
                    .fuelFill(metrics.getFuelFill())
                    .fuelTheft(metrics.getFuelTheft())
                    .fleetSate(metrics.getLatestFleetState())
                    .build());

            // latest event only
            if (!metrics.getEvents().isEmpty()) {
                allEvents.add(metrics.getEvents().getLast());
            }

            totalUsed += metrics.getFuelConsumption();
            totalFill += metrics.getFuelFill();
            totalTheft += metrics.getFuelTheft();
            totalDistance += metrics.getDistanceKm();
            totalRunHours += metrics.getRunHours();

            totalFuelInFleet += metrics.getLatestFuelLevel();
        }

        double avgEfficiency = 0;
        if (totalUsed > 0.1) {
            avgEfficiency = totalDistance > 0.5 ? totalDistance / totalUsed : (totalRunHours > 0 ? totalRunHours / totalUsed : 0);
        }

        return FuelPerformanceDTO.builder()
                .totalFuelConsumption(FuelCalculationHelper.round(totalUsed))
                .totalFuelFill(FuelCalculationHelper.round(totalFill))
                .totalFuelTheft(FuelCalculationHelper.round(totalTheft))
                .avgFuelEfficiency(FuelCalculationHelper.round(avgEfficiency))
                .totalFuelInFleets(FuelCalculationHelper.round(totalFuelInFleet))
                .range(range)
                .eventList(allEvents)
                .fuelData(fuelDataList)
                .build();
    }

    // WEEK | MONTH
    private FuelPerformanceDTO buildPeriodReport(LocalDateTime from, LocalDateTime to, String deviceIdParam, String range) {
        List<String> deviceIds = resolveDeviceIds(deviceIdParam);
        if (deviceIds.isEmpty()) {
            return emptyPeriodReport(range);
        }

        List<DevicePacketData> packets = devicePacketDataRepo
                .findByDeviceIdInAndPacketTimestampBetweenOrderByDeviceIdAscPacketTimestampAsc(deviceIds, from, to);

        // Daily trend (sum across all devices per calendar day)
        Map<LocalDate, List<DevicePacketData>> byDay = packets.stream()
                .collect(Collectors.groupingBy(p -> p.getPacketTimestamp().toLocalDate()));

        List<DailyFuelTrendDTO> dailyTrends = new ArrayList<>();
        double grandUsed = 0, grandFill = 0, grandTheft = 0;
        double grandDistance = 0, grandRunHours = 0;

        List<LocalDate> sortedDays = byDay.keySet().stream().sorted()
                .collect(Collectors.toList());

        for (LocalDate day : sortedDays) {
            List<DevicePacketData> dayPackets = byDay.get(day);
            if (dayPackets == null || dayPackets.isEmpty()) {
                continue;
            }

            // Group day packets by device → calculate each → sum
            Map<String, List<DevicePacketData>> dayByDevice = dayPackets.stream()
                    .collect(Collectors.groupingBy(DevicePacketData::getDeviceId));

            double dayUsed = 0, dayFill = 0, dayTheft = 0;
            double dayDistance = 0, dayRunHours = 0;

            for (List<DevicePacketData> dp : dayByDevice.values()) {
                DeviceFuelMetrics m = FuelCalculationHelper.calculateDeviceMetrics(dp);
                dayUsed += m.getFuelConsumption();
                dayFill += m.getFuelFill();
                dayTheft += m.getFuelTheft();
                dayDistance += m.getDistanceKm();
                dayRunHours += m.getRunHours();
            }

            double dayEff = 0;
            if (dayUsed > 0.1) {
                dayEff = dayDistance > 0.5 ? dayDistance / dayUsed : (dayRunHours > 0 ? dayRunHours / dayUsed : 0);
            }

            dailyTrends.add(DailyFuelTrendDTO.builder()
                    .date(day.format(DATE_FMT))
                    .totalFuelConsumption(FuelCalculationHelper.round(dayUsed))
                    .totalFuelFilled(FuelCalculationHelper.round(dayFill))
                    .totalFuelTheft(FuelCalculationHelper.round(dayTheft))
                    .avgDailyFuelEfficiency(FuelCalculationHelper.round(dayEff))
                    .build());

            grandUsed += dayUsed;
            grandFill += dayFill;
            grandTheft += dayTheft;
            grandDistance += dayDistance;
            grandRunHours += dayRunHours;
        }

        // Vehicle summary (one row per device for whole period)
        Map<String, List<DevicePacketData>> byDevice = packets.stream()
                .collect(Collectors.groupingBy(DevicePacketData::getDeviceId));

        List<VehicleFuelSummaryDTO> vehicleSummaries = new ArrayList<>();
        for (String deviceId : deviceIds) {
            List<DevicePacketData> devicePackets = byDevice.getOrDefault(deviceId, Collections.emptyList());
            if (devicePackets.isEmpty()) {
                continue;
            }

            DeviceFuelMetrics m = FuelCalculationHelper.calculateDeviceMetrics(devicePackets);

            vehicleSummaries.add(VehicleFuelSummaryDTO.builder()
                    .deviceId(deviceId)
                    .fuelConsumption(m.getFuelConsumption())
                    .fuelFill(m.getFuelFill())
                    .fuelTheft(m.getFuelTheft())
                    .avgMileage(m.getEfficiency())
                    .build());
        }

        double avgEfficiency = 0;
        if (grandUsed > 0.1) {
            avgEfficiency = grandDistance > 0.5 ? grandDistance / grandUsed : (grandRunHours > 0 ? grandRunHours / grandUsed : 0);
        }

        return FuelPerformanceDTO.builder()
                .totalFuelConsumption(grandUsed)
                .totalFuelFill(FuelCalculationHelper.round(grandFill))
                .totalFuelTheft(FuelCalculationHelper.round(grandTheft))
                .avgFuelEfficiency(FuelCalculationHelper.round(avgEfficiency))
                .range(range)
                .dailyFuelTrend(dailyTrends)
                .vehicleFuelSummary(vehicleSummaries)
                .build();
    }

    // Helpers
    private List<String> resolveDeviceIds(String deviceIdParam) {
        if (StringUtils.hasText(deviceIdParam)) {
            return Arrays.stream(deviceIdParam.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        }
        // No deviceId supplied → all devices
        return deviceRepo.findAllDistinctDeviceIds();
    }

    private FuelPerformanceDTO emptyDayReport(String range) {
        return FuelPerformanceDTO.builder()
                .totalFuelConsumption(0.0)
                .totalFuelFill(0.0)
                .totalFuelTheft(0.0)
                .avgFuelEfficiency(0.0)
                .range(range)
                .totalFuelInFleets(0.0)
                .eventList(Collections.emptyList())
                .fuelData(Collections.emptyList())
                .build();
    }

    private FuelPerformanceDTO emptyPeriodReport(String range) {
        return FuelPerformanceDTO.builder()
                .totalFuelConsumption(0.0)
                .totalFuelFill(0.0)
                .totalFuelTheft(0.0)
                .avgFuelEfficiency(0.0)
                .range(range)
                .dailyFuelTrend(Collections.emptyList())
                .vehicleFuelSummary(Collections.emptyList())
                .build();
    }
}