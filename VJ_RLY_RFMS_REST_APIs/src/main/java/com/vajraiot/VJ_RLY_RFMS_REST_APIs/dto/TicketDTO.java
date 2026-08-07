package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.enums.Events;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TicketDTO {

    private String deviceId;
    private String status;
    private Events message;
    private LocalDateTime raiseTime;
    private LocalDateTime closeTime;
    private LocalDateTime serverTime;
}
