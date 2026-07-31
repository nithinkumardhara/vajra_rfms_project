package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FleetAnalyticsDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FuelReportDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.TicketDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.exports.CSVReportExport;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.exports.ExcelReportExport;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.exports.PDFReportExport;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FleetAnalyticsService;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.FuelAnalyticsService;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReportsExportController {

    private final FuelAnalyticsService fuelAnalyticsService;
    private final FleetAnalyticsService fleetAnalyticsService;
    private final TicketService ticketService;

    // Fuel Reports
    @GetMapping("/fuelreport/export/pdf/{reportType}/{deviceId}")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable String reportType,
            @RequestParam(required = false) @PathVariable String deviceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        List<FuelReportDTO> reports = getReports(
                reportType,
                deviceId,
                date,
                month,
                startDateTime,
                endDateTime
        );

        byte[] pdf;

        if(deviceId == null || deviceId.isBlank()){
            pdf = PDFReportExport.generateFuelSummaryPdf(reports);
        }else{
            pdf = PDFReportExport.generateFuelReportPdf(reports.getFirst());
        }

        return buildResponse(
                pdf,
                reportType,
                deviceId,
                "pdf",
                "application/pdf"
        );
    }

    @GetMapping("/fuelreport/export/excel/{reportType}/{deviceId}")
    public ResponseEntity<byte[]> exportExcel(
            @PathVariable String reportType,
            @RequestParam(required = false) @PathVariable String deviceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        List<FuelReportDTO> reports = getReports(
                reportType,
                deviceId,
                date,
                month,
                startDateTime,
                endDateTime
        );

        byte[] excel;

        if(deviceId == null || deviceId.isBlank()){
            excel = ExcelReportExport.generateFuelSummaryExcel(reports);
        }else{
            excel = ExcelReportExport.generateFuelReportExcel(reports.get(0));
        }

        return buildResponse(
                excel,
                reportType,
                deviceId,
                "xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    @GetMapping("/fuelreport/export/csv/{reportType}/{deviceId}")
    public ResponseEntity<byte[]> exportCsv(
            @PathVariable String reportType,
            @RequestParam(required = false) @PathVariable String deviceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {

        List<FuelReportDTO> reports = getReports(
                reportType,
                deviceId,
                date,
                month,
                startDateTime,
                endDateTime
        );

        byte[] csv;

        if(deviceId == null || deviceId.isBlank()){
            csv = CSVReportExport.generateFuelSummaryCSV(reports);
        }else{
            csv = CSVReportExport.generateFuelReportCSV(reports.get(0));
        }

        return buildResponse(
                csv,
                reportType,
                deviceId,
                "csv",
                "text/csv"
        );
    }

    private List<FuelReportDTO> getReports(
            String reportType,
            String deviceId,
            LocalDate date,
            YearMonth month,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime){

        return fuelAnalyticsService.getReport(deviceId, reportType, date, month, startDateTime, endDateTime);
    }

    private ResponseEntity<byte[]> buildResponse(
            byte[] file,
            String reportType,
            String deviceId,
            String extension,
            String contentType) {

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename="
                                + reportType.toUpperCase()
                                + "_"
                                + deviceId
                                + "."
                                + extension)
                .header("Content-Type", contentType)
                .body(file);
    }


    // Fleet Reports
    @GetMapping("/fleetreport/export/pdf")
    public ResponseEntity<byte[]> exportFleetPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        // Default Today
        if (startTime == null || endTime == null) {
            endTime = LocalDateTime.now();
            startTime = LocalDate.now().atStartOfDay();
        }

        byte[] pdf;

        List<FleetAnalyticsDTO> analyticsList = fleetAnalyticsService.getAllDeviceAnalytics(startTime, endTime);

        pdf = PDFReportExport.generateFleetSummaryPdf(analyticsList);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + "FLEET_SUMMARY.pdf"
                )
                .header(
                        "Content-Type",
                        "application/pdf"
                )
                .body(pdf);
    }

    @GetMapping("/fleetreport/export/excel")
    public ResponseEntity<byte[]> exportFleetExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        // Default Today
        if (startTime == null || endTime == null) {
            endTime = LocalDateTime.now();
            startTime = LocalDate.now().atStartOfDay();
        }

        byte[] excel;

        List<FleetAnalyticsDTO> analyticsList = fleetAnalyticsService.getAllDeviceAnalytics(startTime, endTime);

        excel = ExcelReportExport.generateFleetSummaryExcel(analyticsList);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + "FLEET_SUMMARY.xlsx"
                )
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }

    @GetMapping("/fleetreport/export/csv")
    public ResponseEntity<byte[]> exportFleetCSV(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        // Default Today
        if (startTime == null || endTime == null) {
            endTime = LocalDateTime.now();
            startTime = LocalDate.now().atStartOfDay();
        }

        byte[] csv;

        List<FleetAnalyticsDTO> analyticsList = fleetAnalyticsService.getAllDeviceAnalytics(startTime, endTime);

        csv = CSVReportExport.generateFleetSummaryCSV(analyticsList);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + "FLEET_SUMMARY.csv"
                )
                .header("Content-Type", "text/csv")
                .body(csv);
    }


//    Tickets
    @GetMapping("/tickets/export/pdf")
    public ResponseEntity<byte[]> exportTicketsPdf(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<TicketDTO> tickets = ticketService.getTicketsForExport(deviceId, startDate, endDate);

        byte[] pdf = PDFReportExport.generateTicketReportPdf(tickets);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=TICKETS_REPORT.pdf"
                )
                .header(
                        "Content-Type",
                        "application/pdf"
                )
                .body(pdf);
    }

    @GetMapping("/tickets/export/excel")
    public ResponseEntity<byte[]> exportTicketsExcel(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        byte[] excel;

        List<TicketDTO> tickets = ticketService.getTicketsForExport(deviceId, startDate, endDate);

        excel = ExcelReportExport.generateTicketsReportExcel(tickets);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + "TICKETS_REPORT.xlsx"
                )
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }

    @GetMapping("/tickets/export/csv")
    public ResponseEntity<byte[]> exportTicketsCSV(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        byte[] csv;

        List<TicketDTO> tickets = ticketService.getTicketsForExport(deviceId, startDate, endDate);

        csv = CSVReportExport.generateTicketsReportCSV(tickets);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + "TICKETS_REPORT.csv"
                )
                .header("Content-Type", "text/csv")
                .body(csv);
    }

}