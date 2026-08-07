//package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;
//
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FuelReportDTO;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceFuelData;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceAlarmStatusRepo;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceFuelDataRepo;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceGPSDataRepo;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class FuelAnalyticsServiceTest {
//
//    @Mock
//    private DeviceGPSDataRepo gpsRepo;
//
//    @Mock
//    private DeviceAlarmStatusRepo alarmRepo;
//
//    @Mock
//    private DeviceRepo deviceRepo;
//
//    @Mock
//    private DeviceFuelDataRepo fuelRepo;
//
//    @InjectMocks
//    private FuelAnalyticsService service;
//
//    private DeviceFuelData fuel1;
//    private DeviceFuelData fuel2;
//    private DeviceGPSData gps;
//    private DeviceAlarmStatus status;
//
//    @BeforeEach
//    void setUp() {
//
//        fuel1 = new DeviceFuelData();
//        fuel1.setDeviceId("D001");
//        fuel1.setFuelLevel(50.0);
//        fuel1.setRunHours(100.0);
//        fuel1.setPacketTimestamp(LocalDateTime.of(2026,1,1,10,0));
//
//        fuel2 = new DeviceFuelData();
//        fuel2.setDeviceId("D001");
//        fuel2.setFuelLevel(45.0);
//        fuel2.setRunHours(105.0);
//        fuel2.setPacketTimestamp(LocalDateTime.of(2026,1,1,11,0));
//
//        gps = new DeviceGPSData();
//        gps.setLatitude(17.22);
//        gps.setLongitude(78.44);
//        gps.setSpeed(12.0);
//        gps.setPacketTimestamp(LocalDateTime.of(2026,1,1,11,0));
//
//        status = new DeviceAlarmStatus();
//        status.setIsIgnitionON(true);
//        status.setIsFuelRefillDetected(false);
//        status.setIsFuelTheftAlert(false);
//        status.setPacketTimestamp(LocalDateTime.of(2026,1,1,11,0));
//    }
//
//    @Test
//    void shouldReturnDailyFuelReport() {
//
//        when(fuelRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(anyString(), any(), any()))
//                .thenReturn(List.of(fuel1, fuel2));
//
//        when(gpsRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(anyString(), any(), any()))
//                .thenReturn(List.of(gps));
//
//        when(alarmRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc(anyString(), any(), any()))
//                .thenReturn(List.of(status));
//
//        List<FuelReportDTO> reports = service.getReport("D001", "DAILY", LocalDate.of(2026,1,1), null, null, null);
//
//        assertNotNull(reports);
//        assertEquals(1,  reports.size());
//
//        FuelReportDTO fuelReport = reports.getFirst();
//        assertEquals("D001",  fuelReport.getDeviceId());
//        assertEquals(50.0, fuelReport.getOpeningFuelLevel());
//        assertEquals(45.0, fuelReport.getClosingFuelLevel());
//    }
//
//    @AfterEach
//    void tearDown() {
//        Mockito.reset(deviceRepo, fuelRepo, alarmRepo, gpsRepo);
//    }
//
//}