package com.vajraiot.VJ_RLY_RFMS_Listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmStatusDTO {

    private Boolean ignitionON;
    private Boolean iotDeviceDisconnect;
    private Boolean vehicleBatteryDisconnect;
    private Boolean iotDeviceTamper;
    private Boolean internalBatteryLow;
    private Boolean externalBatteryLow;
    private Boolean fuelRefill;
    private Boolean fuelTheft;
    private Boolean lowFuelLevel;
    private Boolean vehicleON;
    private Boolean vibrationStatus;
    private Boolean lowBattery;

}
