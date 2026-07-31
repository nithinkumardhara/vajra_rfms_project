package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.AlarmStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlarmStatusService alarmStatusService;

    @Test
    void shouldReturn200() throws Exception {

        when(alarmStatusService.getLatestAlarmStatus("VJ26FMS003")).thenReturn("Success");

        mockMvc.perform(get("/api/alarms/latest")
                .param("deviceId", "VJ26FMS003"))
                .andExpect(status().isOk());
    }

}