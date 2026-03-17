package com.example.sentinel_ingestor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final StringRedisTemplate redisTemplate;
    private static final String STREAM_KEY = "video-jobs";

    public void pushToQueue(String jobId, String url){
        Map<String, String> payload = Map.of(
                "jobId",jobId,
                "videoUrl", url
        );

        MapRecord<String, String, String> record = StreamRecords.newRecord()
                .in(STREAM_KEY)
                .ofMap(payload);

        this.redisTemplate.opsForStream().add(record);
        System.out.println("Pushed Job" + jobId + " to Redis");
    }
}
