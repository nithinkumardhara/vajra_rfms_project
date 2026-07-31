package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.DashboardSummaryDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Device;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceDataSnapshot;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceDataSnapshotRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
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
class DashboardServiceTest {

    @Mock
    private DeviceRepo deviceRepo;

    @Mock
    private DeviceDataSnapshotRepo snapshotRepo;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardSummary(){
        Device d1 = new Device();
        d1.setDeviceId("D01");
        d1.setLastCommunicationTime(LocalDateTime.now().minusHours(1));

        Device d2 = new Device();
        d2.setDeviceId("D02");
        d2.setLastCommunicationTime(LocalDateTime.now().minusHours(6));

        DeviceDataSnapshot s1 = new DeviceDataSnapshot();
        s1.setDeviceId("D01");
        s1.setMovementStatus("MOVING");

        DeviceDataSnapshot s2 = new DeviceDataSnapshot();
        s2.setDeviceId("D02");
        s2.setMovementStatus("STOPPED");

        DeviceDataSnapshot s3 = new DeviceDataSnapshot();
        s3.setDeviceId("D03");
        s3.setMovementStatus("IDLE");

        when(deviceRepo.findAll()).thenReturn(List.of(d1, d2));

        when(snapshotRepo.findAll()).thenReturn(List.of(s1, s2, s3));

        DashboardSummaryDTO result = dashboardService.getDashboardSummary();

        assertNotNull(result);

        assertEquals(1, result.getCommunicating());
        assertEquals(1, result.getNonCommunicating());
        assertEquals(1, result.getMoving());
        assertEquals(1, result.getStopped());
        assertEquals(1, result.getIdle());

        verify(deviceRepo, times(1)).findAll();
        verify(snapshotRepo, times(1)).findAll();
    }

    @Test
    void shouldReturnMovingDevices(){
        DeviceDataSnapshot s1 = new DeviceDataSnapshot();
        s1.setDeviceId("D01");
        s1.setMovementStatus("MOVING");

        Device d1 = new Device();
        d1.setDeviceId("D01");

        when(snapshotRepo.findAll()).thenReturn(List.of(s1));

        when(deviceRepo.findByDeviceIdIn(anyList())).thenReturn(List.of(d1));

        var result = dashboardService.getDevicesByStatus("MOVING");

        assertNotNull(result);
        assertEquals(1, result.get("deviceCount"));
        assertEquals("MOVING", result.get("status"));

    }

    @Test
    void shouldReturnAllDevicesWhenStatusNull(){
        Device  d1 = new Device();
        d1.setDeviceId("D01");

        when(deviceRepo.findAll()).thenReturn(List.of(d1));

        var result = dashboardService.getDevicesByStatus(null);

        assertNotNull(result);
        assertEquals(1, result.get("deviceCount"));
        assertEquals("ALL", result.get("status"));

        verify(deviceRepo, times(1)).findAll();
    }
}