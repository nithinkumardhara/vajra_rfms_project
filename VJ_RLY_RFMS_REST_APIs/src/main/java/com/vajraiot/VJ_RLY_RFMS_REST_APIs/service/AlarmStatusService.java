package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.DeviceAlarmStatusRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmStatusService {

    private final DeviceAlarmStatusRepo deviceAlarmStatusRepo;

    public Object getLatestAlarmStatus(String deviceId) {

        if (deviceId != null && !deviceId.isBlank()) {
            return deviceAlarmStatusRepo.findFirstByDeviceIdOrderByPacketTimestampDesc(deviceId)
                    .orElse(null);
        }

        return deviceAlarmStatusRepo.findLatestAlarmStatusForAllDevices();
    }
}
