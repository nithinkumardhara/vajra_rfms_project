package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FuelAnalysisResultDTO {
    private Double consumption;
    private Double totalRefill;
    private Double totalTheft;
    private Integer refillCount;
    private Integer theftCount;

    private List<RefillEventDTO> refillEvents;
    private List<TheftEventDTO> theftEvents;
    private List<FuelRecordDTO> fuelRecords;
}
