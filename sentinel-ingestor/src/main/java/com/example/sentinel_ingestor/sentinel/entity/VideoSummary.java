package com.example.sentinel_ingestor.sentinel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "video_summaries")
public class VideoSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", unique = true, nullable = false)
    private String jobId;

    @Column(name = "video_id", unique = true, nullable = false)
    private String videoId;

    private String title;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    private LocalDateTime createdAt;
}