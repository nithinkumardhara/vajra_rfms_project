package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DevicePacketData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DevicePacketDataRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FleetPerformanceService {

    private final DevicePacketDataRepo devicePacketDataRepo;
    private final DeviceRepo deviceRepo;

    // Run Hours Summary
    public List<FleetRunHoursDTO> getFleetRunHours(String deviceId, String filter, LocalDate date, Integer year, Integer month) {

        LocalDateTime from;
        LocalDateTime to;

        switch (filter.toUpperCase()) {
            case "TODAY":
                from = LocalDate.now().atStartOfDay();
                to = LocalDate.now().atTime(LocalTime.MAX);
                break;

            case "DAY":
                from = date.atStartOfDay();
                to = date.atTime(LocalTime.MAX);
                break;

            case "WEEK":
                date = (date != null) ? date : LocalDate.now();
                from = date.minusDays(6).atStartOfDay();
                to = date.atTime(LocalTime.MAX);
                break;

            case "MONTH":
                YearMonth ym = YearMonth.of(year, month);
                from = ym.atDay(1).atStartOfDay();
                to = ym.atEndOfMonth().atTime(LocalTime.MAX);
                break;

            default:
                throw new IllegalArgumentException("Invalid filter");
        }

        List<FleetRunHoursDTO> response = new ArrayList<>();

        // Single Vehicle
        if (deviceId != null && !deviceId.isBlank()) {
            List<DevicePacketData> packets = devicePacketDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(deviceId, from, to);
            if (!packets.isEmpty()) {
                response.add(calculateRunHours(deviceId, packets, from, to));
            }

            return response;
        }

        // All Vehicles
        List<String> deviceIds = deviceRepo.findAllDistinctDeviceIds();
        for (String id : deviceIds) {
            List<DevicePacketData> packets = devicePacketDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(id, from, to);
            if (!packets.isEmpty()) {
                response.add(calculateRunHours(id, packets, from, to));
            }
        }

        return response;
    }

    private FleetRunHoursDTO calculateRunHours(String deviceId, List<DevicePacketData> packets, LocalDateTime from, LocalDateTime to) {

        long moving = 0;
        long idle = 0;
        long stopped = 0;
        double totalDistance = 0.0;

        for (int i = 0; i < packets.size() - 1; i++) {
            DevicePacketData current = packets.get(i);
            DevicePacketData next = packets.get(i + 1);

            long seconds = Duration.between(current.getPacketTimestamp(), next.getPacketTimestamp()).getSeconds();

            String fleetState = current.getFleetState();
            if (fleetState == null) {
                continue;
            }

            switch (fleetState) {
                case "MOVING" -> moving += seconds;
                case "IDLE" -> idle += seconds;
                case "STOPPED" -> stopped += seconds;
            }

            if (current.getLatitude() != null && next.getLatitude() != null) {
                totalDistance += haversineKm(
                        current.getLatitude(), current.getLongitude(),
                        next.getLatitude(), next.getLongitude());
            }
        }

        return FleetRunHoursDTO.builder()
                .deviceId(deviceId)
                .movingRunHours(toLocalTime(moving))
                .idleRunHours(toLocalTime(idle))
                .stoppedRunHours(toLocalTime(stopped))
                .totalDistance(round2(totalDistance))
                .period(from.toLocalDate() + " - " + to.toLocalDate())
                .build();
    }

    // Get Trip Summary for only Today | Day
    public FleetTripTimelineDTO getFleetTrips(String deviceId, String filter, LocalDate date) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("deviceId is required for trip summary");
        }

        LocalDate reportDate;
        LocalDateTime from;
        LocalDateTime to;

        switch (filter.toUpperCase()) {
            case "TODAY" -> {
                reportDate = LocalDate.now();
                from = reportDate.atStartOfDay();
                to   = reportDate.atTime(LocalTime.MAX);
            }

            case "DAY" -> {
                if (date == null) throw new IllegalArgumentException("date is required for DAY filter");
                reportDate = date;
                from = date.atStartOfDay();
                to   = date.atTime(LocalTime.MAX);
            }

            default -> throw new IllegalArgumentException("Trips are supported only for TODAY and DAY filters");
        }

        List<DevicePacketData> packets = devicePacketDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(deviceId, from, to);

        List<TripDTO> trips = buildTrips(packets, from, to);
        double totalDistance = trips.stream()
                .mapToDouble(t -> t.getDistance() != null ? t.getDistance() : 0.0)
                .sum();

        return FleetTripTimelineDTO.builder()
                .deviceId(deviceId)
                .reportDate(reportDate)
                .totalDistance(totalDistance)
                .tripCount(trips.size())
                .trips(trips)
                .build();
    }

//      Detects trips from ignition ON/OFF transitions.
//      Handles the midnight-running-vehicle edge case by forcing a trip boundary at end-of-day.
    private List<TripDTO> buildTrips(List<DevicePacketData> packets, LocalDateTime dayStart, LocalDateTime dayEnd) {

        List<TripDTO> trips = new ArrayList<>();
        if (packets.isEmpty()) return trips;

        int currentTripId = 1;
        LocalDateTime tripStart = null;
        Double startLat = null, startLon = null;
        List<DevicePacketData> tripPackets = new ArrayList<>();

        Boolean prevIgnition = null;

        for (DevicePacketData p : packets) {
            Boolean currIgnition = Boolean.TRUE.equals(p.getIsIgnitionON());

            // ---- start of a new trip ----
            if (Boolean.TRUE.equals(currIgnition) && !Boolean.TRUE.equals(prevIgnition)) {
                // close any previous open trip (should not happen, but safety)
                if (tripStart != null) {
                    trips.add(finalizeTrip(currentTripId++, tripStart, p.getPacketTimestamp(),
                            startLat, startLon, p.getLatitude(), p.getLongitude(), tripPackets));
                    tripPackets.clear();
                }
                tripStart = p.getPacketTimestamp();
                startLat  = p.getLatitude();
                startLon  = p.getLongitude();
                tripPackets.add(p);
            }
            // ---- continue an ongoing trip ----
            else if (Boolean.TRUE.equals(currIgnition) && tripStart != null) {
                tripPackets.add(p);
            }
            // ---- end of a trip ----
            else if (!Boolean.TRUE.equals(currIgnition) && tripStart != null) {
                trips.add(finalizeTrip(currentTripId++, tripStart, p.getPacketTimestamp(),
                        startLat, startLon, p.getLatitude(), p.getLongitude(), tripPackets));
                tripStart = null;
                startLat = startLon = null;
                tripPackets.clear();
            }

            prevIgnition = currIgnition;
        }

        // Edge case: vehicle still running at end of the selected day
        if (tripStart != null) {
            DevicePacketData last = packets.get(packets.size() - 1);
            trips.add(finalizeTrip(currentTripId, tripStart, dayEnd, startLat, startLon,
                    last.getLatitude(), last.getLongitude(), tripPackets));
        }

        return trips;
    }

    private TripDTO finalizeTrip(int tripId, LocalDateTime start, LocalDateTime end, Double startLat, Double startLon,
                                 Double endLat, Double endLon, List<DevicePacketData> tripPackets) {

        long durationSec = Duration.between(start, end).getSeconds();
        if (durationSec < 0) durationSec = 0;

        double distanceKm = 0.0;
        double speedSum = 0.0;
        int speedSamples = 0;

        for (int i = 0; i < tripPackets.size(); i++) {
            DevicePacketData cur = tripPackets.get(i);
            if (cur.getSpeed() != null) {
                speedSum += cur.getSpeed();
                speedSamples++;
            }
            if (i > 0) {
                DevicePacketData prev = tripPackets.get(i - 1);
                if (prev.getLatitude() != null && cur.getLatitude() != null) {
                    distanceKm += haversineKm(
                            prev.getLatitude(), prev.getLongitude(),
                            cur.getLatitude(), cur.getLongitude());
                }
            }
        }

        double avgSpeed = speedSamples > 0 ? speedSum / speedSamples : 0.0;

        return TripDTO.builder()
                .tripId(tripId)
                .startTime(start.toLocalTime())
                .endTime(end.toLocalTime())
                .durationHours(toLocalTime(durationSec))
                .distance(round2(distanceKm))
                .avgSpeed(round2(avgSpeed))
                .starLatitude(startLat)
                .starLongitude(startLon)
                .endLatitude(endLat)
                .endLongitude(endLon)
                .build();
    }


    // Get Distance summary for Week | Month
    public FleetPerformanceDTO getFleetDistanceSummary(String deviceId, String filter, LocalDate date,  Integer year, Integer month) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("deviceId is required");
        }

        LocalDateTime from;
        LocalDateTime to;
        String reportRange;

        switch (filter.toUpperCase()) {
            case "WEEK" -> {
                date = (date != null) ? date : LocalDate.now();
                from = date.minusDays(6).atStartOfDay();
                to   = date.atTime(LocalTime.MAX);
                reportRange = from.toLocalDate() + " - " + to.toLocalDate();
            }

            case "MONTH" -> {
                if (year == null || month == null) {
                    throw new IllegalArgumentException("year and month are required for MONTH filter");
                }
                YearMonth ym = YearMonth.of(year, month);
                from = ym.atDay(1).atStartOfDay();
                to   = ym.atEndOfMonth().atTime(LocalTime.MAX);
                reportRange = ym.toString();               // e.g. "2026-08"
            }

            default -> throw new IllegalArgumentException("Distance summary is supported only for WEEK and MONTH filters");
        }

        List<DevicePacketData> packets = devicePacketDataRepo
                .findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(deviceId, from, to);

        // Group packets by date and calculate distance per day
        Map<LocalDate, List<DevicePacketData>> packetsByDate = packets.stream()
                .collect(Collectors.groupingBy(p -> p.getPacketTimestamp().toLocalDate(),
                        TreeMap::new, Collectors.toList()));

        List<FleetDistanceDTO> fleetDistances = new ArrayList<>();
        double totalDistance = 0.0;

        // Ensure every day in the range appears (even if 0 km)
        LocalDate current = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();

        while (!current.isAfter(endDate)) {
            List<DevicePacketData> dayPackets = packetsByDate.getOrDefault(current, Collections.emptyList());
            double dayDistance = calculateDayDistance(dayPackets);

            fleetDistances.add(FleetDistanceDTO.builder()
                    .date(current)
                    .distance(round2(dayDistance))
                    .build());

            totalDistance += dayDistance;
            current = current.plusDays(1);
        }

        return FleetPerformanceDTO.builder()
                .deviceId(deviceId)
                .reportRange(reportRange)
                .totalDistance(round2(totalDistance))
                .fleetDistances(fleetDistances)
                .build();
    }

//    Calculate total distance (km) for one day's packets using Haversine.
    private double calculateDayDistance(List<DevicePacketData> dayPackets) {
        if (dayPackets.size() < 2) return 0.0;

        double distance = 0.0;
        for (int i = 1; i < dayPackets.size(); i++) {
            DevicePacketData prev = dayPackets.get(i - 1);
            DevicePacketData curr = dayPackets.get(i);

            if (prev.getLatitude() != null && curr.getLatitude() != null) {
                distance += haversineKm(
                        prev.getLatitude(), prev.getLongitude(),
                        curr.getLatitude(), curr.getLongitude());
            }
        }
        return distance;
    }

    // ---------- helpers ----------
    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private LocalTime toLocalTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return LocalTime.of((int) (hours % 24), (int) minutes, (int) seconds);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}