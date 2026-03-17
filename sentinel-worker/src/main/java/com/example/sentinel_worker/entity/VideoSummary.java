package com.example.sentinel_worker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_summaries")
public class VideoSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Also add Getters so you can read the data later
    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String videoId;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String summaryText;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String jobId;

    private LocalDateTime createdAt = LocalDateTime.now();

}
