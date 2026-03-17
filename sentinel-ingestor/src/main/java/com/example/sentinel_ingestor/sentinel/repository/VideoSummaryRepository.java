package com.example.sentinel_ingestor.sentinel.repository;

import com.example.sentinel_ingestor.sentinel.entity.VideoSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoSummaryRepository extends JpaRepository<VideoSummary, Long> {

    // For polling status
    Optional<VideoSummary> findByJobId(String jobId);

    // For checking if we already summarized this video (The Cache Check)
    Optional<VideoSummary> findByVideoId(String videoId);
}
