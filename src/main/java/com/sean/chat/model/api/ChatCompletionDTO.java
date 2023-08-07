package com.sean.chat.model.api;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionDTO {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChoiceDTO> choices;
    private UsageDTO usage;        // Getters and Setters

    @Data
    public static class ChoiceDTO {
        private int index;
        private MessageDTO message;
        private String finish_reason;        // Getters and Setters
    }

    @Data
    public static class MessageDTO {
        private String role;
        private String content;        // Getters and Setters
    }

    @Data
    public static class UsageDTO {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}