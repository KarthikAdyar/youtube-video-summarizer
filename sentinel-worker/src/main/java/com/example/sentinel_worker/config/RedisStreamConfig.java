package com.example.sentinel_worker.config;

import com.example.sentinel_worker.service.StreamConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.Map;

@Configuration
@RequiredArgsConstructor

public class RedisStreamConfig {
    private final StreamConsumer streamConsumer;

    @Bean
    public Subscription subscription(RedisConnectionFactory factory) {
        var options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                // REMOVE targetType(Map.class) - let it default to MapRecord
                .build();

        var container = StreamMessageListenerContainer.create(factory, options);

        var subscription = container.receive(
                StreamOffset.create("video-jobs", ReadOffset.lastConsumed()),
                streamConsumer
        );

        container.start();
        return subscription;
    }
}
