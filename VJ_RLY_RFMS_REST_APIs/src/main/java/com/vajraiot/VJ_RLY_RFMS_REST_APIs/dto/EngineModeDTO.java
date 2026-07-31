package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineModeDTO {
    private String deviceId;
    private String period;               // DAILY, WEEKLY, MONTHLY
    private Long timeEngineOn;           // Seconds
    private Long timeEngineOff;
    private Integer ignitionToggleCount; // Number of times ignition turned on/off
    private LocalDateTime lastIgnitionOnTime;
    private LocalDateTime lastIgnitionOffTime;
    private Long idleTime;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
