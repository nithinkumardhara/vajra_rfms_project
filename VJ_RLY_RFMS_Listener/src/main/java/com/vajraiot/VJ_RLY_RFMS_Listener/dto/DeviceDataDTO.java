package com.vajraiot.VJ_RLY_RFMS_Listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDataDTO {

    private String packetVariant;
    private String deviceId;
    private String version;
    private LocalDateTime packetTimeStamp;
    private String imei;
    private String simNumber;
    private Integer signalStrength;
    private LocalDate installationDate;

    // Sections
    private GeneralDataDTO generalData;
    private GPSDataDTO gpsData;
    private FuelDataDTO fuelData;
    private AlarmStatusDTO alarmStatus;
}
