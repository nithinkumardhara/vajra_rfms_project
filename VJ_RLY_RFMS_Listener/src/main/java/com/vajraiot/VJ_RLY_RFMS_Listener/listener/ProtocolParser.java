package com.vajraiot.VJ_RLY_RFMS_Listener.listener;

import com.vajraiot.VJ_RLY_RFMS_Listener.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ProtocolException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ProtocolParser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DeviceDataDTO parse(byte[] packet) throws ProtocolException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(packet);

            if ((char) buffer.get() != '#') {
                throw new ProtocolException("Invalid packet start");
            }

            DeviceDataDTO dto = parseHeader(buffer);

            while (buffer.hasRemaining()) {
                int section = buffer.get() & 0xFF;

                if (section == 0x2E) {
                    break;
                }

                switch (section) {
                    case 0x45 -> parseGeneral(buffer, dto);

                    case 0x50 -> parseGPS(buffer, dto);

                    case 0x34 -> parseFuel(buffer, dto);

                    case 0x51 -> parseAlarm(buffer, dto);

                    default -> log.warn("Unknown section : {}", Integer.toHexString(section));
                }
            }

            return dto;

        } catch (Exception ex) {
            throw new ProtocolException(ex.getMessage());
        }
    }

    private DeviceDataDTO parseHeader(ByteBuffer buffer) {

        DeviceDataDTO dto = new DeviceDataDTO();

        dto.setPacketVariant(readUntilSeparator(buffer));
        dto.setDeviceId(readUntilSeparator(buffer));
        dto.setVersion(readUntilSeparator(buffer));

        String time = readUntilSeparator(buffer);
        String date = readUntilSeparator(buffer);

        dto.setPacketTimeStamp(parseDateTime(date, time));
        dto.setImei(readUntilSeparator(buffer));
        dto.setSimNumber(readUntilSeparator(buffer));
        dto.setSignalStrength(safeInt(readUntilSeparator(buffer)));
        dto.setInstallationDate(parseDate(readUntilSeparator(buffer)));

        return dto;
    }

    private void parseGeneral(ByteBuffer buffer, DeviceDataDTO dto) {

        GeneralDataDTO general = new GeneralDataDTO();

        general.setInternalBatteryVoltage(safeDouble(readAscii(buffer, 5)));
        general.setExternalBatteryVoltage(safeDouble(readAscii(buffer, 5)));
        general.setConsumption(safeDouble(readAscii(buffer, 5)));
        general.setTemperature(safeDouble(readAscii(buffer, 10)));

        dto.setGeneralData(general);
    }

    private void parseGPS(ByteBuffer buffer, DeviceDataDTO dto) {

        GPSDataDTO gps = new GPSDataDTO();

        gps.setGpsFix(safeInt(readUntilSeparator(buffer)));
        gps.setDate(readUntilSeparator(buffer));
        gps.setTime(readUntilSeparator(buffer));
        gps.setLatitude(safeDouble(readUntilSeparator(buffer)));
        gps.setLatitudeDirection(readUntilSeparator(buffer));
        gps.setLongitude(safeDouble(readUntilSeparator(buffer)));
        gps.setLongitudeDirection(readUntilSeparator(buffer));
        gps.setSpeed(safeDouble(readUntilSeparator(buffer)));
        gps.setHeading(safeDouble(readUntilSeparator(buffer)));
        gps.setNoOfSatellites(safeInt(readUntilSeparator(buffer)));
        gps.setAltitude(safeDouble(readUntilSeparator(buffer)));
        gps.setPdop(safeDouble(readUntilSeparator(buffer)));
        gps.setHdop(safeDouble(readUntilSeparator(buffer)));

        dto.setGpsData(gps);
    }

    private void parseFuel(ByteBuffer buffer, DeviceDataDTO dto) {

        FuelDataDTO fuel = new FuelDataDTO();

        fuel.setManufacturerId(safeInt(readAscii(buffer, 2)));
        fuel.setSerialNumber(readAscii(buffer, 13));
        fuel.setFuelLevelHeight(safeDouble(readDynamic(buffer,2))/10);
        fuel.setFuelLevel(safeDouble(readDynamic(buffer, 4))/100);
        fuel.setFuelLevelPercentage(safeDouble(readDynamic(buffer, 2))/10);

        String bits = bytesToBits(buffer, 2);
        int status = Integer.parseInt(bits, 2);

        fuel.setSensorFMSAlarms(status);
        fuel.setVehicleBatteryVoltage(safeDouble(readDynamic(buffer, 2))/100);
        fuel.setRunHours(safeDouble(readDynamic(buffer, 4)));
        fuel.setInFlow(safeDouble(readDynamic(buffer, 4))/100);
        fuel.setOutFlow(safeDouble(readDynamic(buffer, 4))/100);

        dto.setFuelData(fuel);

        AlarmStatusDTO alarm = dto.getAlarmStatus();

        if (alarm == null) {
            alarm = new AlarmStatusDTO();
        }

        parseFMSAlarms(status, alarm);

        dto.setAlarmStatus(alarm);
    }

    private void parseAlarm(ByteBuffer buffer, DeviceDataDTO dto) {

        AlarmStatusDTO alarm = dto.getAlarmStatus();

        if (alarm == null) {
            alarm = new AlarmStatusDTO();
        }

        String bits = bytesToBits(buffer, 4);

        alarm.setIgnitionON(bitPosition(bits, 1));
        alarm.setIotDeviceDisconnect(bitPosition(bits, 3));
        alarm.setVehicleBatteryDisconnect(bitPosition(bits, 8));
        alarm.setIotDeviceTamper(bitPosition(bits, 9));
        alarm.setInternalBatteryLow(bitPosition(bits, 14));
        alarm.setExternalBatteryLow(bitPosition(bits, 15));
        dto.setAlarmStatus(alarm);
    }

    private void parseFMSAlarms(int status, AlarmStatusDTO alarm) {

        alarm.setVibrationStatus(((status >> 2) & 1) == 1);
        alarm.setVehicleON(((status >> 3) & 1) == 1);
        alarm.setLowFuelLevel(((status >> 4) & 1) == 1);
        alarm.setFuelTheft(((status >> 5) & 1) == 1);
        alarm.setFuelRefill(((status >> 6) & 1) == 1);
        alarm.setLowBattery(((status >> 8) & 1) == 1);
    }

    private String readUntilSeparator(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();

        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();

            if (c == ',') {
                break;
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    private String readAscii(ByteBuffer buffer, int length) {
        byte[] data = new byte[length];
        buffer.get(data);

        return new String(data, StandardCharsets.US_ASCII)
                .replace("\0", "").trim();
    }

    private String readDynamic(ByteBuffer buffer, int length){
        byte[] data = new byte[length];
        buffer.get(data);
        long value = 0;

        for(int i=0;i<length;i++){
            value=(value<<8) | (data[i]&0xFF);
        }

        return String.valueOf(value);
    }

    private String bytesToBits(ByteBuffer buffer, int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int value = buffer.get() & 0xFF;

            sb.append(String.format("%8s", Integer.toBinaryString(value))
                    .replace(' ', '0'));
        }
        return sb.toString();
    }

    private boolean bitPosition(String bits, int pos) {
        return new StringBuilder(bits)
                .reverse()
                .charAt(pos) == '1';
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (Exception ex) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String date, String time) {
        try {
            return LocalDateTime.parse(date + " " + time, DATE_TIME_FORMAT);
        } catch (Exception ex) {
            return null;
        }
    }

    private double safeDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int safeInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }
}