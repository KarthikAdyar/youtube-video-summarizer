package com.example.sentinel_ingestor.sentinel.rag;

import com.example.sentinel_ingestor.sentinel.entity.VideoSummary;
import com.example.sentinel_ingestor.sentinel.repository.VideoSummaryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RagService {

    private final VideoSummaryRepository repository;
    private final ChatClient chatClient;

    public RagService(VideoSummaryRepository repository, ChatClient.Builder builder) {
        this.repository = repository;
        this.chatClient = builder.build();
    }

    public String askQuestion(String videoId, String question, String ownerId) {
        // 1. Look up the video summary to get the transcript
        Optional<VideoSummary> opt;
        if (ownerId != null) {
            opt = repository.findByVideoIdAndOwnerId(videoId, ownerId);
        } else {
            opt = repository.findByVideoId(videoId);
        }

        if (opt.isEmpty()) {
            return "No video found. Please summarize the video first.";
        }

        VideoSummary summary = opt.get();
        String transcript = summary.getTranscript();

        // 2. Use transcript if available, otherwise fall back to summary text
        String context;
        String contextType;

        if (transcript != null && !transcript.isBlank()) {
            context = transcript;
            contextType = "transcript";
        } else if (summary.getSummaryText() != null && !summary.getSummaryText().isBlank()) {
            context = summary.getSummaryText();
            contextType = "summary";
        } else {
            return "No transcript or summary available for this video.";
        }

        // Truncate if too long (~500K chars max)
        if (context.length() > 500_000) {
            context = context.substring(0, 500_000);
        }

        // 3. Ask Gemini using the context
        String prompt = """
            You are a helpful assistant that answers questions about YouTube video content.
            
            Here is the %s of the video titled "%s":
            
            ---
            %s
            ---
            
            Answer the following question based ONLY on the content above.
            If the answer cannot be found in the content, say "I couldn't find that information in the video."
            
            Question: %s
            """.formatted(contextType, summary.getTitle(), context, question);

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return answer != null ? answer : "Sorry, I couldn't generate an answer.";
    }
}