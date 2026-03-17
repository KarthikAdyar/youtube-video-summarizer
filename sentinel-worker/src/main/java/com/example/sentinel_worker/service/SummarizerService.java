package com.example.sentinel_worker.service;

import com.example.sentinel_worker.entity.VideoSummary;
import com.example.sentinel_worker.repository.VideoSummaryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SummarizerService {
    private final VideoSummaryRepository repository;
    private final ChatClient chatClient;

    public SummarizerService(VideoSummaryRepository repository , ChatClient.Builder builder){
        this.repository = repository;
        this.chatClient = builder.build();
    }

    public void processSummary(String jobId , String videoId , String title , String transcript){
        String summary = chatClient.prompt()
                .user("Summarise this video transcript " + transcript)
                .call().content();

        VideoSummary videoSummary = new VideoSummary();
        videoSummary.setVideoId(videoId);
        videoSummary.setTitle(title);
        videoSummary.setSummaryText(summary);
        videoSummary.setJobId(jobId);

        repository.save(videoSummary);
        System.out.println("✅ Summary saved to DB for video: " + title);
    }



    public String summarize(String transcript){
        return chatClient.prompt()
                .user("You are an expert content summarizer. " +
                        "Please summarize the following YouTube transcript into 3-5 concise bullet points: " +
                        transcript)
                .call()
                .content();
    }

}