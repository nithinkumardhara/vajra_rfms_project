package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FuelPerformanceDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface FuelReportService {

    FuelPerformanceDTO getTodayReport(String deviceId);
    FuelPerformanceDTO getDayWiseReport(LocalDate date, String deviceId);
    FuelPerformanceDTO getWeeklyReport(LocalDate date, String deviceIds);
    FuelPerformanceDTO getMonthlyReport(YearMonth month, String deviceIds);
    FuelPerformanceDTO getReport(LocalDateTime from, LocalDateTime to, String deviceIds);
}
