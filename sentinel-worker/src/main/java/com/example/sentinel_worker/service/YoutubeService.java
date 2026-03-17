package com.example.sentinel_worker.service;

import io.github.thoroldvix.api.TranscriptApiFactory;
import io.github.thoroldvix.api.YoutubeTranscriptApi;
import org.springframework.stereotype.Service;

@Service
public class YoutubeService {

    public String fetchTranscript(String videoUrl) {
        try {
            String videoId = videoUrl.contains("v=")
                    ? videoUrl.split("v=")[1].split("&")[0]
                    : videoUrl;

            YoutubeTranscriptApi api = TranscriptApiFactory.createDefault();

            // This returns the TranscriptContent
            var transcriptContent = api.listTranscripts(videoId)
                    .findTranscript("en")
                    .fetch();

            StringBuilder fullText = new StringBuilder();

            // FIX: Try getContent() which returns the List of transcript lines
            transcriptContent.getContent().forEach(line ->
                    fullText.append(line.getText()).append(" ")
            );

            return fullText.toString().trim();
        } catch (Exception e) {
            return "Could not retrieve transcript. Error: " + e.getMessage();
        }
    }
}