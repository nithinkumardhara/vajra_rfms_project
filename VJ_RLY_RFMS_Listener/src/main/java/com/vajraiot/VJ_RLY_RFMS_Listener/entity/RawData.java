package com.vajraiot.VJ_RLY_RFMS_Listener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String rawPacketString;

    @Column(columnDefinition = "TEXT")
    private String rawPacketHex;

    private String deviceId;

    private String status;

    private LocalDateTime receivedTime;

}
