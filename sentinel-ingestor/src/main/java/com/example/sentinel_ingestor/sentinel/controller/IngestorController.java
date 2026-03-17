package com.example.sentinel_ingestor.sentinel.controller;

import com.example.sentinel_ingestor.sentinel.dto.VideoRequest;
import com.example.sentinel_ingestor.sentinel.entity.VideoSummary;
import com.example.sentinel_ingestor.sentinel.repository.VideoSummaryRepository;
import com.example.sentinel_ingestor.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IngestorController {

    private final QueueService queueService;
    private final VideoSummaryRepository repository;

    private String extractVideoId(String videoUrl) {
        if (videoUrl != null && videoUrl.contains("v=")) {
            String[] parts = videoUrl.split("v=");
            if (parts.length > 1) {
                return parts[1].split("&")[0];
            }
        }
        return null; // Or throw an exception for invalid URL
    }

    @PostMapping("/summarize")
    public ResponseEntity<?> summarize(@RequestBody VideoRequest request) {
        String videoId = extractVideoId(request.getUrl());
        if (videoId == null) {
            return ResponseEntity.badRequest().body("Invalid YouTube URL");
        }

        // Check if we already have this video summarized
        Optional<VideoSummary> existing = repository.findByVideoId(videoId);

        if (existing.isPresent()) {
            // Return existing summary instantly
            System.out.println("Here");
            System.out.println(ResponseEntity.ok(existing.get()));
            return ResponseEntity.ok(existing.get());
        }
        String jobId = java.util.UUID.randomUUID().toString();

        // Call your service with both required arguments
        queueService.pushToQueue(jobId, request.getUrl());

        // Return the jobId to the frontend so React can show a "Loading..." state for that specific job
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable String jobId) {
        // Look up the summary in your DB using the Job ID
        // If found, return the summary object (title, text, etc.)
        // If not found, return a 404 (telling React to keep waiting)
        System.out.println("Is it here " + jobId);
        return repository.findByJobId(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<VideoSummary>> getHistory() {
        List<VideoSummary> data = repository.findAll();

        return ResponseEntity
                .status(HttpStatus.OK) // Sets status to 200
                .header("X-Sentinel-Version", "1.0") // You can add custom headers
                .body(data); // The actual JSON
    }

    @PostMapping
    public Map<String, String> processVideo(@RequestBody VideoRequest request){
        String jobId = UUID.randomUUID().toString();

        System.out.println("Received URL " + request);
        queueService.pushToQueue(jobId , request.getUrl());

        return Map.of(
                "jobId", jobId,
                "status", "ACCEPTED",
                "message", "Your summary is generated"

        );

    }
}
