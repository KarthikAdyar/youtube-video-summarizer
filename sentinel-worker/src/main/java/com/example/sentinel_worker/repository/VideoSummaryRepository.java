package com.example.sentinel_worker.repository;

import com.example.sentinel_worker.entity.VideoSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VideoSummaryRepository extends JpaRepository<VideoSummary , Long> {
    Optional<VideoSummary> findByVideoId(String videoId);
}
