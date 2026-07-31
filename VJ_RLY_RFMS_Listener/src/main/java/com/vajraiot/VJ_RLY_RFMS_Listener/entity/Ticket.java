package com.vajraiot.VJ_RLY_RFMS_Listener.entity;

import com.vajraiot.VJ_RLY_RFMS_Listener.enums.Events;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="tickets")
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_status_Id")
    private Long id;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private Events message;

    private String status;

    @Column(name = "raise_timestamp")
    private LocalDateTime raiseTimestamp;

    @Column(name = "close_timestamp")
    private LocalDateTime closeTimestamp;

    @Column(name = "server_timestamp")
    private LocalDateTime serverTimestamp;

}
