//package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;
//
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.ApiResponse;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.Device;
//import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceRepo;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DeviceServiceTest {
//
//    @Mock
//    private DeviceRepo deviceRepo;
//
//    @InjectMocks
//    private DeviceService deviceService;
//
//    private Device device1;
//    private Device device2;
//    private List<Device> deviceList;
//
//    @BeforeEach
//    void setUp() {
//        device1 = new Device();
//        device1.setDeviceId("D001");
//
//        device2 = new Device();
//        device2.setDeviceId("D002");
//
//        deviceList = Arrays.asList(device1, device2);
//    }
//
//    @AfterEach
//    void tearDown() {
//        deviceList = null;
//        device1 = null;
//        device2 = null;
//    }
//
//    @Test
//    void shouldReturnGetAllDevices() {
//
//        when(deviceRepo.findAll()).thenReturn(deviceList);
//
//        ApiResponse<Map<String, Object>> result = deviceService.getAllDevices();
//
//        assertTrue(result.isSuccess());
//        assertEquals("Devices retrieved successfully", result.getMessage());
//
//        Map<String, Object> data = result.getData();
//        assertEquals(2, data.get("totalDevices"));
//        assertNotNull(result.getData());
//
//        verify(deviceRepo, times(1)).findAll();
//    }
//
//    @Test
//    void shouldReturnGetDeviceById() {
//
//        when(deviceRepo.findByDeviceId("D001")).thenReturn(Optional.of(device1));
//
//        ApiResponse<Device> result = deviceService.getDeviceById("D001");
//
//        assertTrue(result.isSuccess());
//        assertEquals("Device found", result.getMessage());
//        assertEquals("D001", result.getData().getDeviceId());
//
//        verify(deviceRepo, times(1)).findByDeviceId("D001");
//    }
//
//    @Test
//    void shouldReturnGetDeviceByIdNotFound() {
//        when(deviceRepo.findByDeviceId("D000")).thenReturn(Optional.empty());
//
//        ApiResponse<Device> result = deviceService.getDeviceById("D000");
//
//        assertFalse(result.isSuccess());
//        assertEquals("Device not found", result.getMessage());
//        assertNull(result.getData());
//
//        verify(deviceRepo, times(1)).findByDeviceId("D000");
//    }
//
//    @Test
//    void shouldReturnGetAllDevicesIds() {
//        when(deviceRepo.findAll()).thenReturn(deviceList);
//
//        ApiResponse<List<String>> result = deviceService.getAllDeviceIds();
//
//        assertTrue(result.isSuccess());
//        assertEquals("Device IDs retrieved successfully", result.getMessage());
//        List<String> ids = result.getData();
//
//        assertEquals(2, ids.size());
//        assertTrue(ids.contains("D001"));
//        assertTrue(ids.contains("D002"));
//
//        verify(deviceRepo, times(1)).findAll();
//    }
//}