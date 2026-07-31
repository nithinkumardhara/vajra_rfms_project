package com.vajraiot.VJ_RLY_RFMS_Listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Socket;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceConnection {

    private String deviceId;
    private Socket socket;
    private LocalDateTime connectedAt;
    private LocalDateTime lastCommunication;
    private Integer signalStrength;
    private Boolean isActive;

}