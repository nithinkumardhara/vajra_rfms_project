package com.vajraiot.VJ_RLY_RFMS_REST_APIs.exports;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelReportExport {

    public static byte[] generateFuelReportExcel(FuelReportDTO report) {

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = createHeaderStyle(workbook);

            CellStyle dataStyle = createDataStyle(workbook);

            createReportSheet(workbook, report, headerStyle, dataStyle);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel Export Error", e);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {

        Font font = workbook.createFont();

        font.setBold(true);

        CellStyle style = workbook.createCellStyle();

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static int addSummaryRow(
            Sheet sheet,
            int rowNum,
            String label,
            String value,
            CellStyle style) {

        Row row = sheet.createRow(rowNum);

        Cell c1 = row.createCell(0);

        c1.setCellValue(label);
        c1.setCellStyle(style);

        Cell c2 = row.createCell(1);

        c2.setCellValue(value == null ? "" : value);
        c2.setCellStyle(style);

        return rowNum + 1;
    }

    private static void autoSize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createReportSheet(Workbook workbook, FuelReportDTO report, CellStyle headerStyle, CellStyle dataStyle) {

        Sheet sheet = workbook.createSheet("Fuel Report");

        int rowNum = 0;

        Row title = sheet.createRow(rowNum++);

        title.createCell(0).setCellValue("Fuel Analytics Report");

        rowNum++;

        rowNum = addSummaryRow(
                sheet,rowNum,
                "Device ID",
                report.getDeviceId(),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,rowNum,
                "Report Type",
                report.getReportType(),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,rowNum,
                "Start Time",
                String.valueOf(report.getStartDateTime()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,rowNum,
                "End Time",
                String.valueOf(report.getEndDateTime()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Opening Fuel",
                String.valueOf(report.getOpeningFuelLevel()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Closing Fuel",
                String.valueOf(report.getClosingFuelLevel()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Consumption",
                String.valueOf(report.getTotalConsumption()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Refill Count",
                String.valueOf(report.getRefillCount()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Total Refill",
                String.valueOf(report.getTotalRefillQuantity()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Theft Count",
                String.valueOf(report.getTheftCount()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Theft Quantity",
                String.valueOf(report.getTheftQuantity()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Running Hours",
                String.valueOf(report.getRunningHours()),
                dataStyle);

        rowNum = addSummaryRow(
                sheet,
                rowNum,
                "Distance Travelled",
                String.valueOf(report.getDistanceTraveled()),
                dataStyle);

        rowNum += 2;


        // Refill
        Row refillTitle = sheet.createRow(rowNum++);

        refillTitle.createCell(0).setCellValue("Refill Events");

        String[] refillHeaders = {
                "Timestamp",
                "Before Level",
                "After Level",
                "Refill Qty",
                "Latitude",
                "Longitude",
                "Duration"
        };

        Row refillHeader = sheet.createRow(rowNum++);

        for (int i = 0; i < refillHeaders.length; i++) {

            Cell cell = refillHeader.createCell(i);

            cell.setCellValue(refillHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        if (report.getRefillEvents() != null) {
            for (RefillEventDTO refill : report.getRefillEvents()) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(
                                String.valueOf(refill.getTimestamp()));

                row.createCell(1)
                        .setCellValue(
                                String.valueOf(refill.getBeforeLevel()));

                row.createCell(2)
                        .setCellValue(
                                String.valueOf(refill.getAfterLevel()));

                row.createCell(3)
                        .setCellValue(
                                String.valueOf(refill.getRefillQuantity()));

                row.createCell(4)
                        .setCellValue(
                                String.valueOf(refill.getLatitude()));

                row.createCell(5)
                        .setCellValue(
                                String.valueOf(refill.getLongitude()));

                row.createCell(6)
                        .setCellValue(
                                String.valueOf(refill.getDurationMinutes()));
            }
        }

        rowNum += 2;


        // Theft
        Row theftTitle = sheet.createRow(rowNum++);

        theftTitle.createCell(0).setCellValue("Theft Events");

        String[] theftHeaders = {
                "Timestamp",
                "Theft Qty",
                "Before Level",
                "After Level",
                "Consumption",
                "Latitude",
                "Longitude",
                "Speed",
                "Ignition"
        };

        Row theftHeader = sheet.createRow(rowNum++);

        for (int i = 0; i < theftHeaders.length; i++) {
            Cell cell = theftHeader.createCell(i);

            cell.setCellValue(theftHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        if (report.getTheftEvents() != null) {

            for (TheftEventDTO theft : report.getTheftEvents()) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(
                                String.valueOf(theft.getTimestamp()));

                row.createCell(1)
                        .setCellValue(
                                String.valueOf(theft.getTheftQuantity()));

                row.createCell(2)
                        .setCellValue(
                                String.valueOf(theft.getFuelLevelBefore()));

                row.createCell(3)
                        .setCellValue(
                                String.valueOf(theft.getFuelLevelAfter()));

                row.createCell(4)
                        .setCellValue(
                                String.valueOf(theft.getFuelConsumption()));

                row.createCell(5)
                        .setCellValue(
                                String.valueOf(theft.getLatitude()));

                row.createCell(6)
                        .setCellValue(
                                String.valueOf(theft.getLongitude()));

                row.createCell(7)
                        .setCellValue(
                                String.valueOf(theft.getSpeed()));

                row.createCell(8)
                        .setCellValue(
                                String.valueOf(theft.getIgnitionON()));
            }
        }

        rowNum += 2;


        // Fuel History
        if ("TODAY".equalsIgnoreCase(report.getReportType())) {

            Row fuelTitle = sheet.createRow(rowNum++);
            fuelTitle.createCell(0).setCellValue("Fuel History");

            String[] headers = {
                    "Timestamp",
                    "Fuel Level",
                    "Event Type",
                    "Ignition",
                    "Latitude",
                    "Longitude",
                    "Speed"
            };

            Row header = sheet.createRow(rowNum++);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);

                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            if (report.getFuelReports() != null) {

                for (FuelRecordDTO fuel : report.getFuelReports()) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0)
                            .setCellValue(
                                    String.valueOf(fuel.getTimestamp()));

                    row.createCell(1)
                            .setCellValue(
                                    String.valueOf(fuel.getFuelLevel()));

                    row.createCell(2)
                            .setCellValue(
                                    fuel.getEventType());

                    row.createCell(3)
                            .setCellValue(
                                    String.valueOf(fuel.getIgnitionOn()));

                    row.createCell(4)
                            .setCellValue(
                                    String.valueOf(fuel.getLatitude()));

                    row.createCell(5)
                            .setCellValue(
                                    String.valueOf(fuel.getLongitude()));

                    row.createCell(6)
                            .setCellValue(
                                    String.valueOf(fuel.getSpeed()));
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static byte[] generateFuelSummaryExcel(List<FuelReportDTO> reports) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fuel Summary");

            CellStyle headerStyle = createHeaderStyle(workbook);

            int rowNum = 0;

            Row title = sheet.createRow(rowNum++);

            title.createCell(0).setCellValue("Fuel Analytics Summary");

            rowNum++;

            String[] headers = {
                    "Device ID",
                    "Report Type",
                    "Start Time",
                    "End Time",
                    "Opening Fuel",
                    "Closing Fuel",
                    "Consumption",
                    "Refill Count",
                    "Total Refill",
                    "Theft Count",
                    "Theft Quantity",
                    "Running Hours",
                    "Distance Travelled"
            };

            Row header = sheet.createRow(rowNum++);

            for (int i=0; i<headers.length; i++){
                Cell cell = header.createCell(i);

                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (FuelReportDTO report : reports){
                Row row = sheet.createRow(rowNum++);

                int col = 0;

                row.createCell(col++)
                        .setCellValue(safe(report.getDeviceId()));

                row.createCell(col++)
                        .setCellValue(safe(report.getReportType()));

                row.createCell(col++)
                        .setCellValue(safe(report.getStartDateTime()));

                row.createCell(col++)
                        .setCellValue(safe(report.getEndDateTime()));

                row.createCell(col++)
                        .setCellValue(safe(report.getOpeningFuelLevel()));

                row.createCell(col++)
                        .setCellValue(safe(report.getClosingFuelLevel()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTotalConsumption()));

                row.createCell(col++)
                        .setCellValue(safe(report.getRefillCount()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTotalRefillQuantity()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTheftCount()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTheftQuantity()));

                row.createCell(col++)
                        .setCellValue(safe(report.getRunningHours()));

                row.createCell(col++)
                        .setCellValue(safe(report.getDistanceTraveled()));
            }

            autoSize(sheet, headers.length);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            return out.toByteArray();
        }
        catch(Exception e){
            throw new RuntimeException("Excel Summary Export Error", e);
        }
    }


    //Fleet
    public static byte[] generateFleetSummaryExcel(List<FleetAnalyticsDTO> reports) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fleet Summary");

            CellStyle headerStyle = createHeaderStyle(workbook);

            int rowNum = 0;

            Row title = sheet.createRow(rowNum++);

            title.createCell(0).setCellValue("Fleet Analytics Summary");

            rowNum++;

            String[] headers = {
                    "Device ID",
                    "Status",
                    "Distance",
                    "Moving Duration",
                    "Stopped Duration",
                    "Idle Duration",
                    "Avg Speed"
            };

            Row header = sheet.createRow(rowNum++);

            for (int i=0; i<headers.length; i++){
                Cell cell = header.createCell(i);

                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for ( FleetAnalyticsDTO report : reports){
                Row row = sheet.createRow(rowNum++);

                int col = 0;

                row.createCell(col++)
                        .setCellValue(safe(report.getDeviceId()));

                row.createCell(col++)
                        .setCellValue(safe(report.getMovementStatus()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTotalDistanceTraveled()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTotalMovingDuration()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTotalStoppedDuration()));

                row.createCell(col++)
                        .setCellValue(safe(report.getTotalIdleDuration()));

                row.createCell(col++)
                        .setCellValue(safe(report.getAverageSpeed()));
            }

            autoSize(sheet, headers.length);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            return out.toByteArray();
        }
        catch(Exception e){
            throw new RuntimeException("Excel Summary Export Error", e);
        }
    }


// Tickets Report
    public static byte[] generateTicketsReportExcel(List<TicketDTO> tickets) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tickets Report");

            CellStyle headerStyle = createHeaderStyle(workbook);

            int rowNum = 0;

            Row title = sheet.createRow(rowNum++);

            title.createCell(0).setCellValue("Tickets Analytics");

            rowNum++;

            String[] headers = {
                    "Device ID",
                    "Status",
                    "Message",
                    "Raise Time",
                    "Close Time"
            };

            Row header = sheet.createRow(rowNum++);

            for (int i=0; i<headers.length; i++){
                Cell cell = header.createCell(i);

                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for ( TicketDTO report : tickets){
                Row row = sheet.createRow(rowNum++);

                int col = 0;

                row.createCell(col++)
                        .setCellValue(safe(report.getDeviceId()));

                row.createCell(col++)
                        .setCellValue(safe(report.getStatus()));

                row.createCell(col++)
                        .setCellValue(safe(report.getMessage()));

                row.createCell(col++)
                        .setCellValue(safe(report.getRaiseTime()));

                row.createCell(col++)
                        .setCellValue(safe(report.getCloseTime()));
            }

            autoSize(sheet, headers.length);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            return out.toByteArray();
        }
        catch(Exception e){
            throw new RuntimeException("Excel Export Error", e);
        }
    }


    private static String safe(Object value){
        return value == null ? "" : String.valueOf(value);
    }
}