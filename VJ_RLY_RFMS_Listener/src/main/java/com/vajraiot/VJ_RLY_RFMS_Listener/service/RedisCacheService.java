package com.vajraiot.VJ_RLY_RFMS_Listener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    //Latest Packet Store in cache
    public void saveLatestData(String vehicleId, Object data){
        try {
            redisTemplate.opsForValue().set("device:" + vehicleId, data);
        } catch (Exception e) {
            log.warn("Redis unavailable/down: {}", e.getMessage());
        }
    }

    // Publish live data
    public void publishLatestData(String vehicleId, Object data){
        try {
            redisTemplate.convertAndSend("device:live:" + vehicleId, data);
        } catch (Exception e) {
            log.warn("Redis unavailable: {}", e.getMessage());
        }
    }

}
