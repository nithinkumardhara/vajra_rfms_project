package com.vajraiot.VJ_RLY_RFMS_REST_APIs.config;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.RedisLiveSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final RedisConnectionFactory connectionFactory;
    private final RedisLiveSubscriber subscriber;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        try {
            connectionFactory.getConnection().ping();
        } catch (Exception e) {
            log.warn("Redis unavailable...");
            return null;
        }

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, new PatternTopic("device:live:*"));

        return container;
    }
}