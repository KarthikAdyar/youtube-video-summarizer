package com.example.sentinel_worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
// Change ObjectRecord to MapRecord here
public class StreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final SummarizerService summarizerService;
    private final YoutubeService youtubeService;

    public static String extractVideoId(String videoUrl) {
        // Basic logic to get the 'v' parameter from https://www.youtube.com/watch?v=XXXXXX
        if (videoUrl != null && videoUrl.contains("v=")) {
            return videoUrl.split("v=")[1].split("&")[0];
        }
        return "unknown_" + System.currentTimeMillis(); // Fallback
    }

    @Override
    // Update the parameter type
    public void onMessage(MapRecord<String, String, String> message) {
        // MapRecord is essentially a Map, so we can access values by key directly
        Map<String, String> body = message.getValue();
        String jobId = body.get("jobId");
        String videoUrl = body.get("videoUrl");

        log.info(">>> [MATCHED] Processing Job: {} for URL: {}", jobId, videoUrl);

        // Your logic...
        String videoId = extractVideoId(videoUrl);
        String title = "YouTube Video " + videoId;
        String transcript = youtubeService.fetchTranscript(videoUrl);
        summarizerService.processSummary(jobId , videoId , title , transcript);

    }
}
