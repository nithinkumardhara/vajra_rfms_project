package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceDataSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLiveSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {

        try {
            String channel = new String(message.getChannel());
            log.info("Redis Received Channel={}", channel);

            DeviceDataSnapshot snapshot = objectMapper.readValue(message.getBody(), DeviceDataSnapshot.class);

            String destination = "/topic/live/" + snapshot.getDeviceId();
            log.info("Broadcasting to {}", destination);

            messagingTemplate.convertAndSend(destination, snapshot);
            log.info("Broadcast Success");

        } catch (Exception e) {
            log.error("Redis Subscriber Error", e);
        }
    }
}