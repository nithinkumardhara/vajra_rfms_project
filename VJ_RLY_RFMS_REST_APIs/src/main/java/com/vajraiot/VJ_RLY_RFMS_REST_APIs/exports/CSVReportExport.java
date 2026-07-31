package com.vajraiot.VJ_RLY_RFMS_REST_APIs.exports;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CSVReportExport {

    public static byte[] generateFuelReportCSV(FuelReportDTO report) {
        try {
            StringBuilder csv = new StringBuilder();

            // TITLE
            csv.append("Fuel Analytics Report")
                    .append("\n\n");

            csv.append("Device ID,")
                    .append(report.getDeviceId())
                    .append("\n");

            csv.append("Report Type,")
                    .append(report.getReportType())
                    .append("\n");

            csv.append("Start Time,")
                    .append(report.getStartDateTime())
                    .append("\n");

            csv.append("End Time,")
                    .append(report.getEndDateTime())
                    .append("\n");

            csv.append("Opening Fuel,")
                    .append(report.getOpeningFuelLevel())
                    .append("\n");

            csv.append("Closing Fuel,")
                    .append(report.getClosingFuelLevel())
                    .append("\n");

            csv.append("Consumption,")
                    .append(report.getTotalConsumption())
                    .append("\n");

            csv.append("Refill Count,")
                    .append(report.getRefillCount())
                    .append("\n");

            csv.append("Total Refill,")
                    .append(report.getTotalRefillQuantity())
                    .append("\n");

            csv.append("Theft Count,")
                    .append(report.getTheftCount())
                    .append("\n");

            csv.append("Theft Quantity,")
                    .append(report.getTheftQuantity())
                    .append("\n");

            csv.append("Running Hours,")
                    .append(report.getRunningHours())
                    .append("\n");

            csv.append("Distance Travelled,")
                    .append(report.getDistanceTraveled())
                    .append("\n\n");

            // REFILL EVENTS
            csv.append("Refill Events\n");
            csv.append("Timestamp,Before Level,After Level,Refill Qty,Latitude,Longitude,Duration\n");

            if (report.getRefillEvents() != null) {
                for (RefillEventDTO refill : report.getRefillEvents()) {
                    csv.append(refill.getTimestamp()).append(",")
                            .append(refill.getBeforeLevel()).append(",")
                            .append(refill.getAfterLevel()).append(",")
                            .append(refill.getRefillQuantity()).append(",")
                            .append(refill.getLatitude()).append(",")
                            .append(refill.getLongitude()).append(",")
                            .append(refill.getDurationMinutes())
                            .append("\n");
                }
            }

            csv.append("\n");

            // THEFT EVENTS
            csv.append("Theft Events\n");
            csv.append("Timestamp,Theft Qty,Before Level,After Level,Consumption,Latitude,Longitude,Speed,Ignition\n");

            if (report.getTheftEvents() != null) {
                for (TheftEventDTO theft : report.getTheftEvents()) {
                    csv.append(theft.getTimestamp()).append(",")
                            .append(theft.getTheftQuantity()).append(",")
                            .append(theft.getFuelLevelBefore()).append(",")
                            .append(theft.getFuelLevelAfter()).append(",")
                            .append(theft.getFuelConsumption()).append(",")
                            .append(theft.getLatitude()).append(",")
                            .append(theft.getLongitude()).append(",")
                            .append(theft.getSpeed()).append(",")
                            .append(theft.getIgnitionON())
                            .append("\n");
                }
            }

            csv.append("\n");

            // FUEL HISTORY
            if ("TODAY".equalsIgnoreCase(report.getReportType())) {

                csv.append("Fuel History\n");
                csv.append("Timestamp,Fuel Level,Event Type,Ignition,Latitude,Longitude,Speed\n");

                if (report.getFuelReports() != null) {
                    for (FuelRecordDTO fuel : report.getFuelReports()) {
                        csv.append(fuel.getTimestamp()).append(",")
                                .append(fuel.getFuelLevel()).append(",")
                                .append(fuel.getEventType()).append(",")
                                .append(fuel.getIgnitionOn()).append(",")
                                .append(fuel.getLatitude()).append(",")
                                .append(fuel.getLongitude()).append(",")
                                .append(fuel.getSpeed())
                                .append("\n");
                    }
                }
            }
            return csv.toString().getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("CSV Export Error", e);
        }
    }

    public static byte[] generateFuelSummaryCSV(List<FuelReportDTO> reports) {
        try {
            StringBuilder csv = new StringBuilder();

            csv.append("Fuel Analytics Summary Report")
                    .append("\n\n");

            csv.append("Device ID," +
                            "Report Type," +
                            "Start Time," +
                            "End Time," +
                            "Opening Fuel," +
                            "Closing Fuel," +
                            "Consumption," +
                            "Refill Count," +
                            "Total Refill," +
                            "Theft Count," +
                            "Theft Quantity," +
                            "Running Hours," +
                            "Distance Travelled"
            );

            csv.append("\n");

            for (FuelReportDTO report : reports) {
                csv.append(safe(report.getDeviceId()))
                        .append(",")
                        .append(safe(report.getReportType()))
                        .append(",")
                        .append(safe(report.getStartDateTime()))
                        .append(",")
                        .append(safe(report.getEndDateTime()))
                        .append(",")
                        .append(safe(report.getOpeningFuelLevel()))
                        .append(",")
                        .append(safe(report.getClosingFuelLevel()))
                        .append(",")
                        .append(safe(report.getTotalConsumption()))
                        .append(",")
                        .append(safe(report.getRefillCount()))
                        .append(",")
                        .append(safe(report.getTotalRefillQuantity()))
                        .append(",")
                        .append(safe(report.getTheftCount()))
                        .append(",")
                        .append(safe(report.getTheftQuantity()))
                        .append(",")
                        .append(safe(report.getRunningHours()))
                        .append(",")
                        .append(safe(report.getDistanceTraveled()))
                        .append("\n");
            }
            return csv.toString().getBytes("UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException("CSV Summary Export Error", e);
        }
    }

    private static String safe(Object value){
        return value == null ? "" : String.valueOf(value);
    }

    // Fleet
    public static byte[] generateFleetSummaryCSV(List<FleetAnalyticsDTO> analyticsList) {
        try {
            StringBuilder csv = new StringBuilder();

            csv.append("Fleet Analytics Summary Report")
                    .append("\n\n");

            csv.append("Device ID," +
                            "Status," +
                            "Distance," +
                            "Moving Duration," +
                            "Stopped Duration," +
                            "Idle Duration," +
                            "Avg Speed"
            );

            csv.append("\n");

            for (FleetAnalyticsDTO report : analyticsList) {
                csv.append(safe(report.getDeviceId()))
                        .append(",")
                        .append(safe(report.getMovementStatus()))
                        .append(",")
                        .append(safe(report.getTotalDistanceTraveled()))
                        .append(",")
                        .append(safe(report.getTotalMovingDuration()))
                        .append(",")
                        .append(safe(report.getTotalStoppedDuration()))
                        .append(",")
                        .append(safe(report.getTotalIdleDuration()))
                        .append(",")
                        .append(safe(report.getAverageSpeed()))
                        .append("\n");
            }

            return csv.toString().getBytes("UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException("CSV Summary Export Error", e);
        }
    }

    public static byte[] generateTicketsReportCSV(List<TicketDTO> tickets) {
        try {
            StringBuilder csv = new StringBuilder();

            csv.append("Tickets Analytics Report")
                    .append("\n\n");

            csv.append("Device ID," +
                    "Status," +
                    "Message," +
                    "Raise Time," +
                    "Close Time,"
            );

            csv.append("\n");

            for (TicketDTO report : tickets) {
                csv.append(safe(report.getDeviceId()))
                        .append(",")
                        .append(safe(report.getStatus()))
                        .append(",")
                        .append(safe(report.getMessage()))
                        .append(",")
                        .append(safe(report.getRaiseTime()))
                        .append(",")
                        .append(safe(report.getCloseTime()))
                        .append("\n");
            }
            return csv.toString().getBytes("UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException("CSV Export Error", e);
        }
    }
}