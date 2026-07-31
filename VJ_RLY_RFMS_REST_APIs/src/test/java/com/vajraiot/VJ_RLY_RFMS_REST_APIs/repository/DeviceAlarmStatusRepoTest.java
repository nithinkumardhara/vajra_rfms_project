package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceAlarmStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeviceAlarmStatusRepoTest {

    @Autowired
    private  DeviceAlarmStatusRepo deviceAlarmStatusRepo;

    @Test
    void shouldSaveData(){
        DeviceAlarmStatus deviceAlarmStatus = DeviceAlarmStatus.builder()
                .deviceId("test")
                .packetTimestamp(LocalDateTime.now())
                .build();

        deviceAlarmStatusRepo.save(deviceAlarmStatus);

//        assertEquals(1, deviceAlarmStatusRepo.count());
    }
}