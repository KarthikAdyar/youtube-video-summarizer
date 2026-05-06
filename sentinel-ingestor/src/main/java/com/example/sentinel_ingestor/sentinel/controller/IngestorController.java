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
    public ResponseEntity<?> summarize(@RequestBody VideoRequest request, org.springframework.security.core.Authentication authentication) {
        String videoId = extractVideoId(request.getUrl());
        if (videoId == null) {
            return ResponseEntity.badRequest().body("Invalid YouTube URL");
        }

        String ownerId = authentication != null ? authentication.getName() : null;

        // Check if we already have this video summarized for this user
        Optional<VideoSummary> existing = repository.findByVideoIdAndOwnerId(videoId, ownerId);

        if (existing.isPresent()) {
            // Return existing summary instantly
            System.out.println("Here");
            System.out.println(ResponseEntity.ok(existing.get()));
            return ResponseEntity.ok(existing.get());
        }
        String jobId = java.util.UUID.randomUUID().toString();

        // Call your service with both required arguments
        queueService.pushToQueue(jobId, request.getUrl(), ownerId);

        // Return the jobId to the frontend so React can show a "Loading..." state for that specific job
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable String jobId, org.springframework.security.core.Authentication authentication) {
        // Look up the summary in your DB using the Job ID
        // If found, return the summary object (title, text, etc.)
        // If not found, return a 404 (telling React to keep waiting)
        System.out.println("Is it here " + jobId);
        String ownerId = authentication != null ? authentication.getName() : null;
        return repository.findByJobId(jobId)
                .map(v -> {
                    if (ownerId != null && !ownerId.equals(v.getOwnerId())) return null;
                    return v;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<VideoSummary>> getHistory(org.springframework.security.core.Authentication authentication) {
        String ownerId = authentication != null ? authentication.getName() : null;
        java.util.List<VideoSummary> data = ownerId != null ? repository.findAllByOwnerId(ownerId) : java.util.List.of();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Sentinel-Version", "1.0")
                .body(data);
    }

    @PostMapping
    public Map<String, String> processVideo(@RequestBody VideoRequest request, org.springframework.security.core.Authentication authentication){
        String jobId = UUID.randomUUID().toString();

        System.out.println("Received URL " + request);
        String ownerId = authentication != null ? authentication.getName() : null;
        queueService.pushToQueue(jobId , request.getUrl(), ownerId);

        return Map.of(
                "jobId", jobId,
                "status", "ACCEPTED",
                "message", "Your summary is generated"

        );

    }
}
