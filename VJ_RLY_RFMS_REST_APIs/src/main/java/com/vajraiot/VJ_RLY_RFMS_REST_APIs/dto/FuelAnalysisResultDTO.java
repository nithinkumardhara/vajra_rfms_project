package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
