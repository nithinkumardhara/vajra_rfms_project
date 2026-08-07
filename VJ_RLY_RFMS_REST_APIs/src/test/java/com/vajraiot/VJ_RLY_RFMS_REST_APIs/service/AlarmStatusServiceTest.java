//package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;
//
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceAlarmStatusRepo;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AlarmStatusServiceTest {
//
//    @Mock
//    private DeviceAlarmStatusRepo deviceAlarmStatusRepo;
//
//    @InjectMocks
//    private AlarmStatusService alarmStatusService;
//
//    @Test
//    void shouldReturnLatestAlarmForSingleDevice() {
//        DeviceAlarmStatus alarm = DeviceAlarmStatus.builder()
//                .deviceId("VJ26FMS003")
//                .packetTimestamp(LocalDateTime.now())
//                .isIgnitionON(true)
//                .build();
//
//        when(deviceAlarmStatusRepo.findFirstByDeviceIdOrderByPacketTimestampDesc("VJ26FMS003"))
//                .thenReturn(Optional.of(alarm));
//
//        Object result = alarmStatusService.getLatestAlarmStatus("VJ26FMS003");
//
//        assertNotNull(result);
//        assertEquals(alarm, result);
//
//        verify(deviceAlarmStatusRepo, times(1))
//                .findFirstByDeviceIdOrderByPacketTimestampDesc("VJ26FMS003");
//    }
//
//    @Test
//    void shouldReturnLatestAlarmForAllDevices() {
//        DeviceAlarmStatus alarmStatus = DeviceAlarmStatus.builder()
//                .deviceId("VJ26FMS003")
//                .packetTimestamp(LocalDateTime.now())
//                .build();
//
//        DeviceAlarmStatus alarmStatus2 = DeviceAlarmStatus.builder()
//                .deviceId("VJ26FMS001")
//                .packetTimestamp(LocalDateTime.now())
//                .build();
//
//        when(deviceAlarmStatusRepo.findLatestAlarmStatusForAllDevices())
//                .thenReturn(List.of(alarmStatus, alarmStatus2));
//
//        Object result = alarmStatusService.getLatestAlarmStatus(null);
//
//        assertNotNull(result);
//        assertEquals(List.of(alarmStatus, alarmStatus2), result);
//
//        verify(deviceAlarmStatusRepo, times(1))
//                .findLatestAlarmStatusForAllDevices();
//    }
//}