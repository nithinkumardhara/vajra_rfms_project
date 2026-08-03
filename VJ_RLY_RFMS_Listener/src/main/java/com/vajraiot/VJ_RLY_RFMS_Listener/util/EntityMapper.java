package com.vajraiot.VJ_RLY_RFMS_Listener.util;

import com.vajraiot.VJ_RLY_RFMS_Listener.dto.*;
import com.vajraiot.VJ_RLY_RFMS_Listener.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class EntityMapper {

    public DevicePacketData mapToDevicePacketData(DeviceDataDTO dto, Long rawDataId) {
        try {
            DevicePacketData packetData = DevicePacketData.builder()
                    .rawDataId(rawDataId)
                    .deviceId(dto.getDeviceId())
                    .packetVariant(dto.getPacketVariant())
                    .imei(dto.getImei())
                    .simNumber(dto.getSimNumber())
                    .version(dto.getVersion())
                    .signalStrength(dto.getSignalStrength())
                    .packetTimestamp(dto.getPacketTimeStamp())
                    .serverTimestamp(LocalDateTime.now())
                    .fleetState(calculateFleetState(dto))
                    .build();

            if (dto.getGeneralData() != null) {
                GeneralDataDTO gd = dto.getGeneralData();
                packetData.setInternalBatteryVoltage(gd.getInternalBatteryVoltage());
                packetData.setExternalBatteryVoltage(gd.getExternalBatteryVoltage());
                packetData.setConsumption(gd.getConsumption());
                packetData.setTemperature(gd.getTemperature());
            }

            if (dto.getGpsData() != null) {
                GPSDataDTO gps = dto.getGpsData();
                packetData.setGpsFix(gps.getGpsFix());
                packetData.setLatitude(gps.getLatitude());
                packetData.setLongitude(gps.getLongitude());
                packetData.setLatitudeDirection(gps.getLatitudeDirection());
                packetData.setLongitudeDirection(gps.getLongitudeDirection());
                packetData.setSpeed(gps.getSpeed());
                packetData.setHeading(gps.getHeading());
                packetData.setNoOfSatellites(gps.getNoOfSatellites());
                packetData.setAltitude(gps.getAltitude());
                packetData.setPdop(gps.getPdop());
                packetData.setHdop(gps.getHdop());
            }

            if (dto.getFuelData() != null) {
                FuelDataDTO fuel = dto.getFuelData();
                packetData.setManufacturerId(fuel.getManufacturerId());
                packetData.setSerialNumber(fuel.getSerialNumber());
                packetData.setFuelLevelHeight(fuel.getFuelLevelHeight());
                packetData.setFuelLevel(fuel.getFuelLevel());
                packetData.setFuelLevelPercentage(fuel.getFuelLevelPercentage());
                packetData.setSensorFmsAlarms(fuel.getSensorFMSAlarms());
                packetData.setVehicleBatteryVoltage(fuel.getVehicleBatteryVoltage());
                packetData.setRunHours(fuel.getRunHours());
                packetData.setInFlow(fuel.getInFlow());
                packetData.setOutFlow(fuel.getOutFlow());
            }

            if (dto.getAlarmStatus() != null) {
                AlarmStatusDTO alarm = dto.getAlarmStatus();
                packetData.setIsIgnitionON(alarm.getIgnitionON());
                packetData.setIsIotDeviceDisconnect(alarm.getIotDeviceDisconnect());
                packetData.setIsVehicleBatteryDisconnect(alarm.getVehicleBatteryDisconnect());
                packetData.setIsIotDeviceTamper(alarm.getIotDeviceTamper());
                packetData.setIsInternalBatteryLow(alarm.getInternalBatteryLow());
                packetData.setIsExternalBatteryLow(alarm.getExternalBatteryLow());
                packetData.setIsVibrationStatus(alarm.getVibrationStatus());
                packetData.setIsVehicleON(alarm.getVehicleON());
                packetData.setIsLowFuelLevel(alarm.getLowFuelLevel());
                packetData.setIsFuelTheft(alarm.getFuelTheft());
                packetData.setIsFuelRefill(alarm.getFuelRefill());
                packetData.setIsLowBattery(alarm.getLowBattery());
            }

            return packetData;
        } catch (Exception e) {
            log.error("Error mapping to DevicePacketData: {}", e.getMessage(), e);
            throw e;
        }
    }

    public DeviceGeneralData mapToDeviceGeneralData(DeviceDataDTO dto) {
        if (dto.getGeneralData() == null) {
            return null;
        }

        GeneralDataDTO gd = dto.getGeneralData();

        // Determine battery status
        Boolean isInternalLow = gd.getInternalBatteryVoltage() != null && gd.getInternalBatteryVoltage() < 12.0;
        Boolean isExternalLow = gd.getExternalBatteryVoltage() != null && gd.getExternalBatteryVoltage() < 12.0;


        return DeviceGeneralData.builder()
                .deviceId(dto.getDeviceId())
                .internalBatteryVoltage(gd.getInternalBatteryVoltage())
                .externalBatteryVoltage(gd.getExternalBatteryVoltage())
                .isInternalBatteryLow(isInternalLow)
                .isExternalBatteryLow(isExternalLow)
                .consumption(gd.getConsumption())
                .temperature(gd.getTemperature())
                .packetTimestamp(dto.getPacketTimeStamp())
                .serverTimestamp(LocalDateTime.now())
                .build();
    }

    public DeviceGPSData mapToDeviceGPSData(DeviceDataDTO dto) {
        if (dto.getGpsData() == null) {
            return null;
        }
        GPSDataDTO gps = dto.getGpsData();

        return DeviceGPSData.builder()
                .deviceId(dto.getDeviceId())
                .gpsFix(gps.getGpsFix())
                .latitude(gps.getLatitude())
                .longitude(gps.getLongitude())
                .latitudeDirection(gps.getLatitudeDirection())
                .longitudeDirection(gps.getLongitudeDirection())
                .speed(gps.getSpeed())
                .heading(gps.getHeading())
                .altitude(gps.getAltitude())
                .noOfSatellites(gps.getNoOfSatellites())
                .pdop(gps.getPdop())
                .hdop(gps.getHdop())
                .fleetState(calculateFleetState(dto))
                .gpsDate(gps.getDate())
                .gpsTime(gps.getTime())
                .packetTimestamp(dto.getPacketTimeStamp())
                .serverTimestamp(LocalDateTime.now())
                .build();
    }


    public DeviceFuelData mapToDeviceFuelData(DeviceDataDTO dto) {
        if (dto.getFuelData() == null) {
            return null;
        }

        FuelDataDTO fuel = dto.getFuelData();

        return DeviceFuelData.builder()
                .deviceId(dto.getDeviceId())
                .manufacturerId(fuel.getManufacturerId())
                .serialNumber(fuel.getSerialNumber())
                .fuelLevelHeight(fuel.getFuelLevelHeight())
                .fuelLevel(fuel.getFuelLevel())
                .fuelLevelPercentage(fuel.getFuelLevelPercentage())
                .inFlow(fuel.getInFlow())
                .outFlow(fuel.getOutFlow())
                .runHours(fuel.getRunHours())
                .sensorFmsAlarms(fuel.getSensorFMSAlarms())
                .packetTimestamp(dto.getPacketTimeStamp())
                .serverTimestamp(LocalDateTime.now())
                .build();
    }

    public DeviceAlarmStatus mapToDeviceAlarmStatus(DeviceDataDTO dto) {
        if (dto.getAlarmStatus() == null) {
            return null;
        }

        AlarmStatusDTO alarm = dto.getAlarmStatus();

        return DeviceAlarmStatus.builder()
                .deviceId(dto.getDeviceId())
                .isIgnitionON(alarm.getIgnitionON())
                .isVehicleON(alarm.getVehicleON())
                .isIotDeviceDisconnect(alarm.getIotDeviceDisconnect())
                .isVehicleBatteryDisconnect(alarm.getVehicleBatteryDisconnect())
                .isIotDeviceTamper(alarm.getIotDeviceTamper())
                .isInternalBatteryLow(alarm.getInternalBatteryLow())
                .isExternalBatteryLow(alarm.getExternalBatteryLow())
                .isLowFuelLevel(alarm.getLowFuelLevel())
                .isVibrationDetected(alarm.getVibrationStatus())
                .isFuelTheftAlert(alarm.getFuelTheft())
                .isFuelRefillDetected(alarm.getFuelRefill())
                .packetTimestamp(dto.getPacketTimeStamp())
                .serverTimestamp(LocalDateTime.now())
                .build();
    }

    private String calculateFleetState(DeviceDataDTO dto) {
        if (dto.getGpsData() == null) {
            return null;
        }
        Boolean ignitionOn = dto.getAlarmStatus() != null ? dto.getAlarmStatus().getIgnitionON() : false;

        if (Boolean.TRUE.equals(ignitionOn)) {
            Double speed = dto.getGpsData().getSpeed();

            if (speed != null && speed > 5.0) {
                return "MOVING";
            }

            return "IDLE";
        }

        return "STOPPED";
    }

}
