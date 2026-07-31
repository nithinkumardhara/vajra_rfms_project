package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceDataSnapshot;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceDataSnapshotRepo;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceGPSDataRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTrackServiceTest {

    @Mock
    private DeviceDataSnapshotRepo deviceDataSnapshotRepo;

    @Mock
    private DeviceGPSDataRepo deviceGPSDataRepo;

    @InjectMocks
    private DeviceTrackService deviceTrackService;

    @Test
    void shouldGetLiveData() {
        DeviceDataSnapshot snapshot = new DeviceDataSnapshot();
        snapshot.setDeviceId("D001");
        snapshot.setMovementStatus("MOVING");

        when(deviceDataSnapshotRepo.findByDeviceId("D001")).thenReturn(Optional.of(snapshot));

        ApiResponse<Map<String, Object>> result = deviceTrackService.getLiveGPS("D001");

        assertNotNull(result);
        assertEquals("LiveGPS retrieved successfully", result.getMessage());
        Map<String, Object> response = result.getData();
        assertEquals("D001", response.get("deviceId"));
        assertEquals("MOVING", response.get("status"));

        verify(deviceDataSnapshotRepo, times(1)).findByDeviceId("D001");
    }

    @Test
    void shouldGetHistoryGPSData() {
        DeviceGPSData gps1 = new DeviceGPSData();
        gps1.setDeviceId("D001");
        gps1.setLatitude(1.0);

        DeviceGPSData gps2 = new DeviceGPSData();
        gps2.setDeviceId("D001");
        gps2.setLatitude(2.0);

        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 12, 0);

        when(deviceGPSDataRepo.findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end))
                .thenReturn(List.of(gps1, gps2));

        ApiResponse<Map<String, Object>> result = deviceTrackService.getHistoryGPS("D001", start, end);

        assertNotNull(result);
        assertEquals("History GPS retrieved successfully", result.getMessage());
        assertEquals("D001", result.getData().get("deviceId"));

        Map<String, Object> data = result.getData();
        assertEquals(2, data.get("totalPoints"));

        verify(deviceGPSDataRepo, times(1)).findByDeviceIdAndPacketTimestampBetweenOrderByPacketTimestampAsc("D001", start, end);
    }
}