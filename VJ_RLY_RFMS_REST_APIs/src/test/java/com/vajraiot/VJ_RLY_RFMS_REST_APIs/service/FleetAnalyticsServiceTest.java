package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.EngineModeDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.FleetAnalyticsDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceAlarmStatusRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceGPSDataRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FleetAnalyticsServiceTest {

    @Mock
    private DeviceGPSDataRepo deviceGPSDataRepo;

    @Mock
    private DeviceRepo deviceRepo;

    @Mock
    private DeviceAlarmStatusRepo alarmStatusRepo;

    @InjectMocks
    private FleetAnalyticsService service;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().minusHours(2);
        end = LocalDateTime.now();
    }

//    @Test
//    void shouldGetDeviceAnalytics() {
//
//        DeviceGPSData gps1 = new DeviceGPSData();
//        gps1.setDeviceId("D001");
//        gps1.setLatitude(17.3850);
//        gps1.setLongitude(78.4867);
//        gps1.setSpeed(30.0);
//        gps1.setMovementStatus("MOVING");
//        gps1.setPacketTimestamp(start);
//
//        DeviceGPSData gps2 = new DeviceGPSData();
//        gps2.setDeviceId("D001");
//        gps2.setLatitude(17.3950);
//        gps2.setLongitude(78.4967);
//        gps2.setSpeed(40.0);
//        gps2.setMovementStatus("MOVING");
//        gps2.setPacketTimestamp(start.plusMinutes(12));
//
//        when(deviceGPSDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end))
//                .thenReturn(List.of(gps1, gps2));
//
//        FleetAnalyticsDTO result = service.getFleetAnalytics("D001", start, end, true);
//
//        assertNotNull(result);
//        assertEquals("D001", result.getDeviceId());
//        assertEquals(35.0, result.getAverageSpeed());
//        assertTrue(result.getTotalDistanceTraveled() > 0);
//        assertEquals(1, result.getTotalMoving());
//
//        verify(deviceGPSDataRepo, times(1))
//                .findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end);
//    }

    @Test
    void shouldGetDeviceFleetAnalytics_Nodata() {

        when(deviceGPSDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end))
                .thenReturn(List.of());

        FleetAnalyticsDTO result = service.getFleetAnalytics("D001", start, end, true);

        assertNotNull(result);
        assertEquals("D001", result.getDeviceId());
        assertNull(result.getCurrentLatitude());
        assertNull(result.getTotalDistanceTraveled());
    }

    @Test
    void shouldGetAllDeviceFleetAnalytics() {

        when(deviceRepo.findAllDistinctDeviceIds()).thenReturn(List.of("D001"));

        DeviceGPSData gps1 = new DeviceGPSData();
        gps1.setDeviceId("D001");
        gps1.setLatitude(17.3850);
        gps1.setLongitude(78.4867);
        gps1.setSpeed(30.0);
        gps1.setMovementStatus("MOVING");
        gps1.setPacketTimestamp(start);

        when(deviceGPSDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end))
                .thenReturn(List.of(gps1));

        List<FleetAnalyticsDTO> result = service.getAllDeviceAnalytics(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("D001", result.getFirst().getDeviceId());
    }

//    @Test
//    void testGetEngineModeAnalysis() {
//
//        DeviceAlarmStatus alarm1 = new DeviceAlarmStatus();
//        alarm1.setDeviceId("D001");
//        alarm1.setIsVehicleON(true);
//        alarm1.setPacketTimestamp(start);
//
//        DeviceAlarmStatus alarm2 = new DeviceAlarmStatus();
//        alarm2.setDeviceId("D001");
//        alarm2.setIsVehicleON(false);
//        alarm2.setPacketTimestamp(start.plusMinutes(30));
//
//        when(alarmStatusRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end))
//                .thenReturn(List.of(alarm2, alarm1));
//
//        when(deviceGPSDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end))
//                .thenReturn(List.of());
//
//        EngineModeDTO result = service.getEngineModeAnalysis("D001", start, end, "DAILY");
//
//        assertNotNull(result);
//        assertEquals("D001", result.getDeviceId());
//        // 30 mins = 1800 sec
//        assertEquals(1800, result.getTimeEngineOn());
//        assertEquals(1, result.getIgnitionToggleCount());
//        assertNotNull(result.getLastIgnitionOnTime());
//    }

}
