package com.vajraiot.VJ_RLY_RFMS_Listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralDataDTO {

    private Double internalBatteryVoltage;
    private Double ExternalBatteryVoltage;
    private Double consumption;
    private Double temperature;

}
