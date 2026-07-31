package com.vajraiot.VJ_RLY_RFMS_REST_APIs.exports;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
public class PDFReportExport {

    // Fuel
    public static byte[] generateFuelReportPdf(FuelReportDTO report) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4.rotate(),
                    20,
                    20,
                    20,
                    20);

            PdfWriter.getInstance(document, out);

            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

            Paragraph title = new Paragraph("Fuel Analytics Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            // Summary Table
            PdfPTable summaryTable = new PdfPTable(2);

            summaryTable.setWidthPercentage(60);
            summaryTable.setWidths(new float[]{2f, 3f});

            addSummaryRow(summaryTable, "Device ID", report.getDeviceId());
            addSummaryRow(summaryTable, "Report Type", report.getReportType());
            addSummaryRow(summaryTable, "Start Time", String.valueOf(report.getStartDateTime()));
            addSummaryRow(summaryTable, "End Time", String.valueOf(report.getEndDateTime()));
            addSummaryRow(summaryTable, "Tank Capacity", String.valueOf(report.getFuelTankCapacity()));
            addSummaryRow(summaryTable, "Opening Fuel", String.valueOf(report.getOpeningFuelLevel()));
            addSummaryRow(summaryTable, "Closing Fuel", String.valueOf(report.getClosingFuelLevel()));
            addSummaryRow(summaryTable, "Consumption", String.valueOf(report.getTotalConsumption()));
            addSummaryRow(summaryTable, "Refill Count", String.valueOf(report.getRefillCount()));
            addSummaryRow(summaryTable, "Total Refill", String.valueOf(report.getTotalRefillQuantity()));
            addSummaryRow(summaryTable, "Theft Count", String.valueOf(report.getTheftCount()));
            addSummaryRow(summaryTable, "Total Theft", String.valueOf(report.getTheftQuantity()));
            addSummaryRow(summaryTable, "Running Hours", String.valueOf(report.getRunningHours()));
            addSummaryRow(summaryTable, "Distance Travelled", String.valueOf(report.getDistanceTraveled()));

            document.add(summaryTable);

            document.add(new Paragraph(" "));

            // Refill Events
            if (report.getRefillEvents() != null && !report.getRefillEvents().isEmpty()) {

                Paragraph refillTitle = new Paragraph("Refill Events",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));

                document.add(refillTitle);
                document.add(new Paragraph(" "));

                PdfPTable refillTable = new PdfPTable(7);
                refillTable.setWidthPercentage(100);

                addHeader(refillTable, "Timestamp");
                addHeader(refillTable, "Before");
                addHeader(refillTable, "After");
                addHeader(refillTable, "Refill Qty");
                addHeader(refillTable, "Latitude");
                addHeader(refillTable, "Longitude");
                addHeader(refillTable, "Duration");

                for (RefillEventDTO refill : report.getRefillEvents()) {

                    refillTable.addCell(String.valueOf(refill.getTimestamp()));
                    refillTable.addCell(String.valueOf(refill.getBeforeLevel()));
                    refillTable.addCell(String.valueOf(refill.getAfterLevel()));
                    refillTable.addCell(String.valueOf(refill.getRefillQuantity()));
                    refillTable.addCell(String.valueOf(refill.getLatitude()));
                    refillTable.addCell(String.valueOf(refill.getLongitude()));
                    refillTable.addCell(String.valueOf(refill.getDurationMinutes()));
                }

                document.add(refillTable);
                document.add(new Paragraph(" "));
            }

            // Theft Events
            if (report.getTheftEvents() != null && !report.getTheftEvents().isEmpty()) {

                Paragraph theftTitle = new Paragraph("Theft Events", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));

                document.add(theftTitle);
                document.add(new Paragraph(" "));

                PdfPTable theftTable = new PdfPTable(9);
                theftTable.setWidthPercentage(100);

                addHeader(theftTable, "Timestamp");
                addHeader(theftTable, "Theft Qty");
                addHeader(theftTable, "Before");
                addHeader(theftTable, "After");
                addHeader(theftTable, "Consumption");
                addHeader(theftTable, "Latitude");
                addHeader(theftTable, "Longitude");
                addHeader(theftTable, "Speed");
                addHeader(theftTable, "Ignition");

                for (TheftEventDTO theft : report.getTheftEvents()) {

                    theftTable.addCell(String.valueOf(theft.getTimestamp()));
                    theftTable.addCell(String.valueOf(theft.getTheftQuantity()));
                    theftTable.addCell(String.valueOf(theft.getFuelLevelBefore()));
                    theftTable.addCell(String.valueOf(theft.getFuelLevelAfter()));
                    theftTable.addCell(String.valueOf(theft.getFuelConsumption()));
                    theftTable.addCell(String.valueOf(theft.getLatitude()));
                    theftTable.addCell(String.valueOf(theft.getLongitude()));
                    theftTable.addCell(String.valueOf(theft.getSpeed()));
                    theftTable.addCell(String.valueOf(theft.getIgnitionON()));
                }

                document.add(theftTable);
                document.add(new Paragraph(" "));
            }

            // Fuel History
            if ("TODAY".equalsIgnoreCase(report.getReportType()) && report.getFuelReports() != null && !report.getFuelReports().isEmpty()) {

                Paragraph fuelTitle = new Paragraph("Fuel History", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));

                document.add(fuelTitle);
                document.add(new Paragraph(" "));

                PdfPTable fuelTable = new PdfPTable(7);
                fuelTable.setWidthPercentage(100);
                fuelTable.setHeaderRows(1);

                addHeader(fuelTable, "Timestamp");
                addHeader(fuelTable, "Fuel");
                addHeader(fuelTable, "Event");
                addHeader(fuelTable, "Ignition");
                addHeader(fuelTable, "Latitude");
                addHeader(fuelTable, "Longitude");
                addHeader(fuelTable, "Speed");

                for (FuelRecordDTO row : report.getFuelReports()) {

                    fuelTable.addCell(String.valueOf(row.getTimestamp()));
                    fuelTable.addCell(String.valueOf(row.getFuelLevel()));
                    fuelTable.addCell(row.getEventType());
                    fuelTable.addCell(String.valueOf(row.getIgnitionOn()));
                    fuelTable.addCell(String.valueOf(row.getLatitude()));
                    fuelTable.addCell(String.valueOf(row.getLongitude()));
                    fuelTable.addCell(String.valueOf(row.getSpeed()));
                }
                document.add(fuelTable);
            }
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }



    public static byte[] generateFuelSummaryPdf(List<FuelReportDTO> reports){
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4.rotate());

            PdfWriter.getInstance(document, out);

            document.open();

            Paragraph title = new Paragraph(
                    "Fuel Analytics Summary Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
                    );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(11);
            table.setWidthPercentage(100);

            addHeader(table,"Device");
            addHeader(table,"Type");
            addHeader(table,"Opening");
            addHeader(table,"Closing");
            addHeader(table,"Consumption");
            addHeader(table,"Refill Count");
            addHeader(table,"Total Refill");
            addHeader(table,"Theft Count");
            addHeader(table,"Total Theft");
            addHeader(table,"Running Hours");
            addHeader(table,"Distance");

            for(FuelReportDTO report : reports){

                table.addCell(
                        safe(report.getDeviceId())
                );

                table.addCell(
                        safe(report.getReportType())
                );

                table.addCell(
                        safe(report.getOpeningFuelLevel())
                );

                table.addCell(
                        safe(report.getClosingFuelLevel())
                );

                table.addCell(
                        safe(report.getTotalConsumption())
                );

                table.addCell(
                        safe(report.getRefillCount())
                );

                table.addCell(
                        safe(report.getTotalRefillQuantity())
                );

                table.addCell(
                        safe(report.getTheftCount())
                );

                table.addCell(
                        safe(report.getTheftQuantity())
                );

                table.addCell(
                        safe(report.getRunningHours())
                );

                table.addCell(
                        safe(report.getDistanceTraveled())
                );
            }
            document.add(table);

            document.close();

            return out.toByteArray();
        }
        catch(Exception e){
            throw new RuntimeException("Error generating summary pdf", e);
        }
    }


    //FLEET ALL DEVICE SUMMARY
    public static byte[] generateFleetSummaryPdf(List<FleetAnalyticsDTO> fleets) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4.rotate());

            PdfWriter.getInstance(document, out);

            document.open();

            Paragraph title = new Paragraph(
                            "Fleet Analytics Summary",
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
                    );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);

            table.setWidthPercentage(100);

            addHeader(table,"Device Id");
            addHeader(table,"Status");
            addHeader(table,"Distance");
            addHeader(table,"Moving Time");
            addHeader(table,"Stopped Time");
            addHeader(table,"Idle Time");
            addHeader(table,"Avg Speed");

            for(FleetAnalyticsDTO fleet : fleets){

                table.addCell(safe(fleet.getDeviceId()));
                table.addCell(safe(fleet.getMovementStatus()));
                table.addCell(safe(fleet.getTotalDistanceTraveled()));
                table.addCell(safe(fleet.getTotalMovingDuration()));
                table.addCell(safe(fleet.getTotalStoppedDuration()));
                table.addCell(safe(fleet.getTotalIdleDuration()));
                table.addCell(safe(fleet.getAverageSpeed()));
            }

            document.add(table);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating summary pdf", e);
        }
    }

    // Helpers
    private static void addHeader(PdfPTable table, String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setBackgroundColor(new Color(52, 73, 94));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);

        table.addCell(cell);
    }

    private static void addSummaryRow(PdfPTable table, String key, String value) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key));

        keyCell.setBackgroundColor(new Color(230, 230, 230));

        table.addCell(keyCell);
        table.addCell(value == null ? "" : value);
    }

    //Tickets Report
    public static byte[] generateTicketReportPdf(List<TicketDTO> tickets) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4.rotate());

            PdfWriter.getInstance(document, out);

            document.open();

            // Title
            Paragraph title = new Paragraph("Ticket Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
            );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);
            document.add(new Paragraph(" "));

            // Table
            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(100);

            table.setWidths(new float[]{
                            2f,
                            2f,
                            3f,
                            3f,
                            3f
                    }
            );

            addHeader(table, "Device Id");
            addHeader(table, "Status");
            addHeader(table, "Message");
            addHeader(table, "Raise Time");
            addHeader(table, "Close Time");

            for (TicketDTO ticket : tickets) {
                table.addCell(safe(ticket.getDeviceId()));
                table.addCell(safe(ticket.getStatus()));
                table.addCell(ticket.getMessage() == null ? "" : ticket.getMessage().name());
                table.addCell(safe(ticket.getRaiseTime()));
                table.addCell(safe(ticket.getCloseTime()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating ticket pdf", e);
        }
    }

    private static String safe(Object value){
        return value == null ? "" : String.valueOf(value);
    }

}
