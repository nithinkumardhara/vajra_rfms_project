package com.vajraiot.VJ_RLY_RFMS_REST_APIs.util;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DevicePacketData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public final class FuelCalculationHelper {

    private static final double THRESHOLD = 2.0;

    private FuelCalculationHelper() { }

    // Core calculation for one device in a time window
    public static DeviceFuelMetrics calculateDeviceMetrics(List<DevicePacketData> packets) {
        if (packets == null || packets.isEmpty()) {
            return DeviceFuelMetrics.empty();
        }

        DevicePacketData first = packets.getFirst();
        DevicePacketData last = packets.getLast();

        double startLevel = Optional.ofNullable(first.getFuelLevel()).orElse(0.0);
        double endLevel = Optional.ofNullable(last.getFuelLevel()).orElse(0.0);

        double fuelConsumption = 0.0;
        double totalFill = 0.0;
        double totalTheft = 0.0;
        double totalRunHours = 0.0;
        double totalDistance = 0.0;

        List<FuelEventDTO> events = new ArrayList<>();

        DevicePacketData prev = first;

        for (int i = 1; i < packets.size(); i++) {
            DevicePacketData curr = packets.get(i);

            // Distance
            if (prev.getLatitude() != null && curr.getLatitude() != null) {
                totalDistance += DistanceCalculateUtil.haversineDistance(prev.getLatitude(), prev.getLongitude(), curr.getLatitude(), curr.getLongitude());
            }

            // Run hours difference
            if (prev.getRunHours() != null && curr.getRunHours() != null && curr.getRunHours() >= prev.getRunHours()) {
                totalRunHours += (curr.getRunHours() - prev.getRunHours());
            }

            double prevLevel = Optional.ofNullable(prev.getFuelLevel()).orElse(0.0);
            double currLevel = Optional.ofNullable(curr.getFuelLevel()).orElse(0.0);
            double delta = currLevel - prevLevel;

            boolean isFillFlag = Boolean.TRUE.equals(curr.getIsFuelRefill());
            boolean isTheftFlag = Boolean.TRUE.equals(curr.getIsFuelTheft());
            boolean isLowFlag = Boolean.TRUE.equals(curr.getIsLowFuelLevel());
            boolean isIgnitionOn = Boolean.TRUE.equals(curr.getIsIgnitionON());

            // Low fuel event
            if (isLowFlag) {
                events.add(buildEvent(curr, "Low", prevLevel, currLevel, Math.abs(delta), prev.getPacketTimestamp()));
            }

            // Time difference
            long seconds = 0;
            if (prev.getPacketTimestamp() != null && curr.getPacketTimestamp() != null) {
                seconds = Duration.between(prev.getPacketTimestamp(), curr.getPacketTimestamp()).getSeconds();
            }
            double minutes = Math.max(seconds / 60.0, 0.1);
            double dropRate = (delta < 0) ? Math.abs(delta) / minutes : 0;

            // FILL Detection
            if (delta >= THRESHOLD || isFillFlag) {
                double volume = Math.max(delta, 0.0);
                totalFill += volume;
                events.add(buildEvent(curr, "Fill", prevLevel, currLevel, volume, prev.getPacketTimestamp()));
            }
            // THEFT Detection
            else if ((isTheftFlag || (delta <= -THRESHOLD && !isIgnitionOn) || (delta < 0 && dropRate >= 4))) {
                    double volume = Math.abs(Math.min(delta, 0.0));
                    totalTheft += volume;
                    events.add(buildEvent(curr, "Theft", prevLevel, currLevel, volume, prev.getPacketTimestamp()));
            }
            // CONSUMPTION
            else if (delta < 0) {
                    fuelConsumption += Math.abs(delta);
            }

            prev = curr;
        }

        // fuel consumption = (start - end)
        fuelConsumption = Math.max(0.0, fuelConsumption);

        double efficiency = 0.0;
        if (fuelConsumption > 0.1 && totalDistance > 0.5) {
            efficiency = fuelConsumption / totalDistance;   // KM/L
        }

        return DeviceFuelMetrics.builder()
                .deviceId(first.getDeviceId())
                .fuelConsumption(round(fuelConsumption))
                .fuelFill(round(totalFill))
                .fuelTheft(round(totalTheft))
                .efficiency(round(efficiency))
                .distanceKm(round(totalDistance))
                .runHours(round(totalRunHours))
                .latestFuelLevel(endLevel)
                .latestFleetState(last.getFleetState())
                .events(events)
                .build();
    }

    private static FuelEventDTO buildEvent(DevicePacketData packet, String type, double startLevel, double endLevel, double volume, LocalDateTime prevTs) {
        return FuelEventDTO.builder()
                .deviceId(packet.getDeviceId())
                .eventType(type)
                .eventTime(packet.getPacketTimestamp() != null ? packet.getPacketTimestamp().toLocalTime() : null)
                .fuelVolume(round(volume))
                .latitude(packet.getLatitude())
                .longitude(packet.getLongitude())
                .startFuelLevel(round(startLevel))
                .endFuelLevel(round(endLevel))
                .build();
    }

    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}